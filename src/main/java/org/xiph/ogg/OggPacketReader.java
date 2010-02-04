/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xiph.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class OggPacketReader {
	private InputStream inp;
	private Iterator<OggPacketData> it;
	
	public OggPacketReader(InputStream inp) {
		this.inp = inp;
	}

	/**
	 * Returns the next packet in the file, or
	 *  null if no more packets remain.
	 * Call {@link OggPacket#isBeginningOfStream()}
	 *  to detect if it is the first packet in the
	 *  stream or not, and use
	 *  {@link OggPacket#getSid()} to track which
	 *  stream it belongs to.
	 */
	public OggPacket getNextPacket() throws IOException {
		// If we have a whole packet ready to go,
		//  just use that
		OggPacketData leftOver = null;
		if(it != null && it.hasNext()) {
			OggPacketData packet = it.next();
			if(packet instanceof OggPacket) {
				return (OggPacket)packet;
			}
			leftOver = packet;
		}

		// Find the next page, from which
		//  to get our next packet from
		int searched = 0;
		int pos = -1;
		boolean found = false;
		int r;
		while(searched < 65536 && !found) {
			r = inp.read();
			if(r == -1) {
				// No more data
				return null;
			}
			
			switch(pos) {
			case -1:
				if(r == (int)'O') {
					pos = 0;
				}
				break;
			case 0:
				if(r == (int)'g') {
					pos = 1;
				} else {
					pos = -1;
				}
				break;
			case 1:
				if(r == (int)'g') {
					pos = 2;
				} else {
					pos = -1;
				}
				break;
			case 2:
				if(r == (int)'S') {
					found = true;
				} else {
					pos = -1;
				}
				break;
			}
			
			if(!found) {
				searched++;
			}
		}
		
		if(!found) {
			throw new IOException("Next ogg packet header not found after searching " + searched + " bytes");
		}
		
		searched -= 3; // OggS
		if(searched > 0) {
			System.err.println("Warning - had to skip " + searched + " bytes of junk data before finding the next packet header");
		}
		
		// Create the page, and prime the iterator on it
		OggPage page = new OggPage(inp);
		it = page.getPacketIterator(leftOver);
		return getNextPacket();
	}
}
