/*
 * Copyright 2016 alexis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.helper.Log;
import org.traccar.helper.StringFinder;

/**
 *
 * @author alexis
 */
public class TotemImageProtocolDecoder extends BaseProtocolDecoder{
    public TotemImageProtocolDecoder(TotemProtocol protocol) {
        super(protocol);
    }
    
    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.readableBytes() < 10) {
            return null;
        }

        int beginIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("$V"));
        if (beginIndex == -1) {
            return msg;
        } else if (beginIndex > buf.readerIndex()) {
            buf.readerIndex(beginIndex);
        }
  
        buf.skipBytes(2); // header
        String imei = buf.readBytes(15).toString(Charset.defaultCharset()); 
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }
        
        int picNumber =     Integer.parseInt(buf.readBytes(5).toString(StandardCharsets.US_ASCII)); 
        int totalPackage =  Integer.parseInt(buf.readBytes(3).toString(StandardCharsets.US_ASCII));
        int seqNumber =     Integer.parseInt(buf.readBytes(3).toString(StandardCharsets.US_ASCII));
        
        if(seqNumber == 1){
            String date = buf.readBytes(12).toString(StandardCharsets.US_ASCII); 
            String pos = buf.readBytes(19).toString(StandardCharsets.US_ASCII);
        }

        int flagIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("#\r\n"));
        if ((flagIndex - buf.readerIndex()) <= buf.readableBytes()) {
            String hs = ChannelBuffers.hexDump(buf, buf.readerIndex(), flagIndex - buf.readerIndex());
//            System.out.println(hs);
            Log.info(hs);
        }
        
        return null;
    }
}
