/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.resource.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {

	// the buffer size for reading data
	private static final int BUFFER_SIZE = 16384;

	/**
	 * Writes all the contents of a Reader to a Writer.
	 * 
	 * @param reader the reader to read from
	 * @param writer the writer to write to
	 */
	public static void copy(Reader reader, Writer writer) throws IOException {
		char[] buf = new char[BUFFER_SIZE];
		int num = 0;

		while ((num = reader.read(buf, 0, buf.length)) != -1) {
			writer.write(buf, 0, num);
		}
	}

	/**
	 * Writes all the contents of a Reader to a Writer.
	 * 
	 * @param reader the reader to read from
	 * @param writer the writer to write to
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int num = 0;

		while ((num = input.read(buf, 0, buf.length)) != -1) {
			output.write(buf, 0, num);
		}
	}

	/**
	 * Writes all the contents of an InputStream to a Writer.
	 * 
	 * @param input the input stream to read from
	 * @param writer the writer to write to
	 */
	public static void copy(InputStream input, Writer writer) throws IOException {
		copy(new InputStreamReader(input), writer);
	}

	/**
	 * Writes all the contents of an InputStream to an OutStream.
	 * 
	 * @param input the input stream to read from
	 * @param output the output stream to write to
	 */
	public static void copy(InputStream input, OutputStream output, boolean closeStreams) throws IOException {
		try {
			copy(input, output);
		} finally {
			if (closeStreams) {
				close(input);
				close(output);
			}
		}
	}

	/**
	 * Close the input stream
	 * @param stream the input stream to close
	 */
	public static void close(InputStream stream) {
		
		if (stream != null) {

			try {
				stream.close();
			} catch (IOException e) {
				// Nothing to do
			}
		}
	}

	/**
	 * Close the output stream
	 * @param stream the output stream to close
	 */
	public static void close(OutputStream stream) {
		
		if (stream != null) {

			try {
				stream.close();
			} catch (IOException e) {
				// Nothing to do
			}
		}
	}
}
