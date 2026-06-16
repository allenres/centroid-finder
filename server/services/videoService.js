import fs from 'fs';
import path from 'path';
import { spawn, exec } from 'child_process';
import { v4 as uuidv4 } from 'uuid';

// Scans the directory for valid videos
export const getAvailableVideos = async () => {
    return new Promise((resolve, reject) => {
        //Reads all the files from env.VIDEOS_DIR then error if failed otherwise array of file names
        fs.readdir(process.env.VIDEOS_DIR, (err, files) => {
            //If reading fails stop immediately
            if (err) return reject(err);

            //Loop through files keep vaild ones
            const videoFiles = files.filter(file => {

                //Get the extension changes to lower case
                const ext = path.extname(file).toLowerCase();

                //Return only valid ones
                return ext === '.mp4' || ext === '.mov';
            });

            //Resolve promise
            resolve(videoFiles);
        });
    });
};

// Generates thumbnail data stream via FFmpeg
export const getThumbnailStream = (filename, callback) => {
    //Build the video Path /sampleInput + ensantina.mp4
    const videoPath = path.join(process.env.VIDEOS_DIR, filename);

    //Checks if video exists in the filesystem
    if (!fs.existsSync(videoPath)) {
        //If missing throw error
        return callback(new Error("Video file not found"), null);
    }
    //Terminal command string creation
    /*
        -ss get 1 second into the video, 
        -i "${videoPath}" input video path
        -vframes 1 get one video frame
        -f image2pipe sends through a stream/pipe
        -vcodec mjpeg encode output as jpeg image data
        -Send outpuit to stdout
    */
    const ffmpegCmd = `ffmpeg -ss 00:00:01 -i "${videoPath}" -vframes 1 -f image2pipe -vcodec mjpeg pipe:1`;
    //Run command encode as buffer because images are binary
    exec(ffmpegCmd, { encoding: 'buffer' }, (err, stdout) => {
        //if fails send error back
        if (err) return callback(err, null);
        //sucess, null means no error and here is the image buffer
        callback(null, stdout);
    });
}

// Starts the detached Java CLI application background run
export const startProcessingJob = (filename, targetColor, threshold) => {
    //Generate unique Job ID
    const jobId = uuidv4();
    const shortJobId = jobId.split('-')[0]; // Shorten for easier reference

    //Generate date
    const timestamp = new Date().toISOString().replace(/[:T]/g, '-').split('.')[0]; // Replace colons and T  Remove milliseconds

    //Build paths 
    const inputPath = path.join(process.env.VIDEOS_DIR, filename); //Input path /sampleInput
    const baseName = path.parse(filename).name; //Base name of the file without extension
    const outputCsvName = `${baseName}-${timestamp}-${shortJobId}.csv`; //Output csv name
    const outputPath = path.join(process.env.RESULTS_DIR, outputCsvName); // Output path to the  {outputCsvName}.csv
    const relativeOutputPath = path.relative(process.cwd(), outputPath).replace(/\\/g, '/');

    //Check if video exists throw error if not
    if (!fs.existsSync(inputPath)) {
        throw new Error("Target video file does not exist.");
    }

    //Json file, and initial status file
    const statusFilePath = path.join(process.env.STATUS_DIR, `${jobId}.json`);
    const logFilePath = path.join(process.env.STATUS_DIR, `${jobId}.log`);

    // Write initial state file
    fs.writeFileSync(statusFilePath, JSON.stringify({ status: "processing" }));

    // Send standard stream out/err down to local log files
    const logFileDescriptor = fs.openSync(logFilePath, 'a');

    //Run jar with these parameters
    const child = spawn('java', [
        '-jar',
        process.env.JAR_PATH,
        inputPath,
        outputPath,
        targetColor,
        threshold
    ], {
        //Makes sure to run independently from node.js
        detached: true,
        stdio: ['ignore', logFileDescriptor, logFileDescriptor]
    });

    // If the spawn fails
    child.on('error', (err) => {
        fs.writeFileSync(statusFilePath, JSON.stringify({
            status: "error",
            error: err.message
        }));
    });

    //Runs when java process exits
    child.on('close', (code) => {
        const finalStatus = code === 0
            ? { status: "done", result: `${relativeOutputPath}` }
            : { status: "error", error: `Error processing video: Unexpected ffmpeg error (Exit code: ${code})` };

        fs.writeFileSync(statusFilePath, JSON.stringify(finalStatus));
    });

    child.unref();
    return jobId;
}

// Fetches current state JSON profile from disk
export const getJobStatus = (jobId) => {
    //Create status file path
    const statusFilePath = path.join(process.env.STATUS_DIR, `${jobId}.json`);

    //check to see if file exists if not return null
    if (!fs.existsSync(statusFilePath)) {
        return null;
    }

    //Read the file completely returns a string "{"status":"done","result":"/results/ensantina.mp4.csv"}"
    const rawData = fs.readFileSync(statusFilePath, 'utf8');
    //Parse rawData to json
    return JSON.parse(rawData);
}

// Generates a 5-second preview GIF via FFmpeg for hover states
export const getPreviewGIF = (filename, callback) => {
    // Build the video Path
    const videoPath = path.join(process.env.VIDEOS_DIR, filename);

    // Checks if video exists in the filesystem
    if (!fs.existsSync(videoPath)) {
        return callback(new Error("Video file not found"), null);
    }

    /*
        FFmpeg Flags Break Down:
        -ss 00:00:00        : Start from the very beginning of the video
        -t 5                : Stop processing after 5 seconds
        -i "${videoPath}"   : Input video file path
        -vf "..."           : Video filtergraph:
           - fps=12         : Downsample the framerate to 12 FPS to keep the file size reasonable
           - scale=320:-1   : Scale width to 320px, keeping the aspect ratio automatic (-1)
           - split [a][b]; [a] palettegen [p]; [b][p] paletteuse: Generates a custom 256-color palette from the video stream dynamically for vibrant output
        -f gif              : Encode the output container layout as standard GIF formats
        pipe:1              : Redirect the output binary stream directly to Node's stdout
    */
    const ffmpegCmd = `ffmpeg -ss 00:00:00 -t 5 -i "${videoPath}" -vf "fps=12,scale=320:-1,split[a][b];[a]palettegen[p];[b][p]paletteuse" -f gif pipe:1`;

    // Run command, matching the binary buffer wrapper logic used in getThumbnailStream
    exec(ffmpegCmd, { encoding: 'buffer', maxBuffer: 10 * 1024 * 1024 }, (err, stdout) => {
        // If execution fails, pass the error back
        if (err) return callback(err, null);
        
        // Success: pass null for the error context and the binary GIF buffer
        callback(null, stdout);
    });
};