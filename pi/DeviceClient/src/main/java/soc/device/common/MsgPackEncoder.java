package soc.device.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by liyan on 16-10-30.
 */
public class MsgPackEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws IOException {
        out.writeBytes(new MessagePack().write(msg));
    }
}