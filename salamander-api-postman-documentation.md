## GET Videos

Retrieves a list of all available videos stored on the server.

### Endpoint

`GET http://localhost:3000/api/videos`

### Request Parameters

No query parameters, path parameters, or request body required.

### Headers

| Header | Value | Required |
| --- | --- | --- |
| Content-Type | application/json | No |

### Response

Returns a JSON array of video objects available on the server.

#### Success Response — `200 OK`

``` json
[
    "ensantina.mp4",
    "ensantina2.mp4"
]

 ```

#### Error Responses

| Status Code | Description |
| --- | --- |
| `500 Internal Server Error` | Error reading video directory |

### Notes

- This endpoint returns all videos currently available on the server.
    
- Use the returned filenames or IDs with other endpoints such as **GET Thumbnail** or **POST Video** to process specific videos.

## GET Thumbnail

Retrieves the thumbnail image for a specified video file.

### Endpoint

`GET http://localhost:3000/thumbnail/:filename`

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `filename` | string | Yes | The filename of the video (e.g., `ensantina.mp4`) |

### Example Request

```
GET http://localhost:3000/thumbnail/ensantina.mp4

 ```

### Headers

No special headers required.

### Response

Returns the thumbnail image file (typically JPEG or PNG) for the specified video.

#### Success Response — `200 OK`

- **Content-Type:** `image/jpeg` or `image/png`
    
- **Body:** Binary image data representing the video thumbnail.
    

#### Error Responses

| Status Code | Description |
| --- | --- |
| `500 Internal Server Error` | Error generating thumbnail |

### Notes

- The `:filename` path parameter must match an existing video file on the server. Use the **GET Videos** endpoint to retrieve a list of available filenames.
    
- The thumbnail is typically extracted from the first frame of the video.

## POST Video

Submits a video file for processing. The processing applies a color transformation based on the specified tarGET color and threshold parameters.

### Endpoint

`POST http://localhost:3000/process/:filename`

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `filename` | string | Yes | The filename of the video to process (e.g., `ensantina2.mp4`) |

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `tarGETColor` | string | Yes | Hex color code (without `#`) to use as the tarGET color for processing (e.g., `4E0007`) |
| `threshold` | integer | Yes | Numeric threshold value that controls the sensitivity of the color transformation (e.g., `50`) |

### Example Request

```
POST http://localhost:3000/process/ensantina2.mp4?tarGETColor=4E0007&threshold=50

 ```

### Request Body

No request body required.

### Headers

No special headers required.

### Response

Returns a job object containing a unique process ID that can be used to track the status of the video processing job.

#### Success Response — `202 Accepted`

``` json
{
    "jobId": "2f045640-197d-4e77-aad8-f2aa63bfdde8"
}

 ```

#### Error Responses

| Status Code | Description |
| --- | --- |
| `400 Bad Request` | Missing or invalid query parameters (`tarGETColor` or `threshold`) |
| `500 Internal Server Error` | Server encountered an unexpected error while initiating the processing job |

### Notes

- The `tarGETColor` parameter should be provided as a hex string **without** the `#` prefix.
    
- After submitting, use the returned `jobId` with the **GET Status** endpoint to poll for processing progress.
    
- Processing is asynchronous — the response is returned immediately while the job runs in the background.

## GET Status

Retrieves the current processing status of a previously submitted video job.

### Endpoint

`GET http://localhost:3000/process/:jobId/status`

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `jobId` | string (UUID) | Yes | The unique job ID returned by the **POST Video** endpoint (e.g., `e579895e-3fcf-4112-9018-36b6b73ddbb8`) |

### Example Request

```
GET http://localhost:3000/process/e579895e-3fcf-4112-9018-36b6b73ddbb8/status

 ```

### Headers

No special headers required.

### Response

Returns a JSON object describing the current state of the processing job.

#### Success Response — `200 OK`

``` json
{
    "status": "done",
    "result": "/results/ensantina2.mp4.csv"
}

 ```

#### Error Responses

| Status Code | Description |
| --- | --- |
| `404 Not Found` | No job found with the specified `jobId` |
| `500 Internal Server Error` | Server encountered an unexpected error while retrieving job status |

### Notes

- Poll this endpoint periodically after submitting a job via **POST Video** to track progress.
    
- When `status` is done, the processed video output should be available for retrieval.
    
- The `progress` field (0–100) indicates the percentage of processing completed.