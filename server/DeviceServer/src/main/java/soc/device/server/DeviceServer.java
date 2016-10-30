package soc.device.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soc.device.common.MsgPackDecoder;
import soc.device.common.MsgPackEncoder;
import soc.device.server.handler.ServerHandler;

/**
 * Created by liyan on 16-10-30.
 */
public class DeviceServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServer.class);
    private static final int MAX_LENGTH_FIELD_LENGTH = 2;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        LOGGER.info("Start Device Server ...");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            ChannelFuture server = bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65536, 0, MAX_LENGTH_FIELD_LENGTH, 0, MAX_LENGTH_FIELD_LENGTH));
                            pipeline.addLast("msgPackDecoder",new MsgPackDecoder());
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(MAX_LENGTH_FIELD_LENGTH));
                            pipeline.addLast("msgPackEncoder",new MsgPackEncoder());
                            pipeline.addLast("server",new ServerHandler());
                        }

                    }).bind(SERVER_IP,SERVER_PORT).sync();
            LOGGER.info("Device Server Started");
            server.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(null,e);
        }finally {
            try {
                bossGroup.shutdownGracefully();
            } catch (Exception e) {
                LOGGER.warn(null,e);
            }
            try {
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                LOGGER.warn(null,e);
            }
        }
    }

}
