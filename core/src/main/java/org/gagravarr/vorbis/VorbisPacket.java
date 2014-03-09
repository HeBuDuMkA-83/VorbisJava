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
package org.gagravarr.vorbis;

import org.gagravarr.ogg.HighLevelOggStreamPacket;
import org.gagravarr.ogg.OggPacket;

/**
 * Parent of all Vorbis packets
 */
public abstract class VorbisPacket extends HighLevelOggStreamPacket {
   public static final int TYPE_INFO = 1;
   public static final int TYPE_COMMENTS = 3;
   public static final int TYPE_SETUP = 5;
   
   protected static final int HEADER_LENGTH_METADATA = 7;
   protected static final int HEADER_LENGTH_AUDIO = 0;
   
   protected VorbisPacket(OggPacket oggPacket) {
       super(oggPacket);
   }
   protected VorbisPacket() {
       super();
   }

   /**
    * How big is the header on this packet?
    * For Metadata packets it's normally 7 bytes,
    *  otherwise for audio packets there is no header.
    */
   protected abstract int getHeaderSize();
   
   protected void populateMetadataHeader(byte[] b, int type, int dataLength) {
       // TODO Refactor me
       VorbisPacketFactory.populateMetadataHeader(b, type, dataLength);
   }
}