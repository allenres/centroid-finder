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
    const videoPath = path.join(process.env.VIDEOS_DIR, filename);

    if (!fs.existsSync(videoPath)) {
        return callback(new Error("Video file not found"), null);
    }

    const ffmpegCmd = `ffmpeg -ss 00:00:01 -i "${videoPath}" -vframes 1 -f image2pipe -vcodec mjpeg pipe:1`;
    exec(ffmpegCmd, { encoding: 'buffer' }, (err, stdout) => {
        if (err) return callback(err, null);
        callback(null, stdout);
    });
}

// Starts the detached Java CLI application background run
export const startProcessingJob = (filename, targetColor, threshold) => {
    const inputPath = path.join(process.env.VIDEOS_DIR, filename);
    const outputCsvName = `${filename}.csv`;
    const outputPath = path.join(process.env.RESULTS_DIR, outputCsvName);

    if (!fs.existsSync(inputPath)) {
        throw new Error("Target video file does not exist.");
    }

    const jobId = uuidv4();
    const statusFilePath = path.join(process.env.STATUS_DIR, `${jobId}.json`);
    const logFilePath = path.join(process.env.STATUS_DIR, `${jobId}.log`);

    // Write initial state file
    fs.writeFileSync(statusFilePath, JSON.stringify({ status: "processing" }));

    // Send standard stream out/err down to local log files
    const logFileDescriptor = fs.openSync(logFilePath, 'a');

    const child = spawn('java', [
        '-jar',
        process.env.JAR_PATH,
        inputPath,
        outputPath,
        targetColor,
        threshold
    ], {
        detached: true,
        stdio: ['ignore', logFileDescriptor, logFileDescriptor]
    });

    child.on('close', (code) => {
        const finalStatus = code === 0
            ? { status: "done", result: `/results/${outputCsvName}` }
            : { status: "error", error: `Error processing video: Unexpected ffmpeg error (Exit code: ${code})` };

        fs.writeFileSync(statusFilePath, JSON.stringify(finalStatus));
    });

    child.unref();
    return jobId;
}

// Fetches current state JSON profile from disk
export const getJobStatus = (jobId) => {
    const statusFilePath = path.join(process.env.STATUS_DIR, `${jobId}.json`);

    if (!fs.existsSync(statusFilePath)) {
        return null;
    }

    const rawData = fs.readFileSync(statusFilePath, 'utf8');
    return JSON.parse(rawData);
}