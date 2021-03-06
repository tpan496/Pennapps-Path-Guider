/*
Copyright (c) Microsoft Corporation
All rights reserved. 
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.
THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pantianlei.pathguider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class SpeechClientREST {

  private static final String URL = "https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=en-US&format=simple";


  private HttpURLConnection connect() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true); 
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000");
    connection.setRequestProperty("Accept", "application/json;text/xml");
    connection.setRequestProperty("Ocp-Apim-Subscription-Key", "274f23b628ba487abac7d06c5c3b99c8");
    connection.setChunkedStreamingMode(0); // 0 == default chunk size
    connection.connect();

    return connection;
  }

  private String getResponse(HttpURLConnection connection) throws IOException {
//    System.out.println(connection.getResponseCode());
//    System.out.println(connection.getResponseMessage());
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new RuntimeException(String.format("Something went wrong, server returned: %d (%s)",
          connection.getResponseCode(), connection.getResponseMessage()));
    }

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      return reader.lines().collect(Collectors.joining());
    }
  }

  private HttpURLConnection upload(InputStream is, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) != -1) {
        output.write(buffer, 0, length);
      }
      output.flush();
    }
    return connection;
  }

  private HttpURLConnection upload(Path filepath, HttpURLConnection connection) throws IOException {
    try (OutputStream output = connection.getOutputStream()) {
      Files.copy(filepath, output);
    }
    return connection;
  }

  public String process(InputStream is) throws IOException {
    return getResponse(upload(is, connect()));
  }


}
