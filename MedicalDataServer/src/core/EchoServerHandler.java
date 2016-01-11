package core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


import RequestWork.RequestHandler;
import io.netty.buffer.*;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

	
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		
		
		
		
		
		ByteBuf buf = (ByteBuf) msg;
		
		byte[] bytesreq = new byte[buf.readableBytes()];
		buf.readBytes(bytesreq);
		
		
		//String return1 = RSA.enc();
		
		
		
		
		
        
		
        //Aes aes = new Aes();
        
		//byte[] decodeDataFromBase64String = Base64.getDecoder().decode(bytesreq);
		
		//byte[] decryptedtest = aes.decrypt(decodeDataFromBase64String);
		
		
		
		
		
		
	    Object result = RequestHandler.requestsHandler(new String(bytesreq));
	
	    //byte[] encryptedansw = aes.encrypt( ((String)result).getBytes() );
	    
	    //byte[] encodeDataToBase64String = Base64.getEncoder().encode(encryptedansw);
		
		
		
		
		ctx.writeAndFlush(Unpooled.wrappedBuffer(((String) result).getBytes()/*encodeDataToBase64String*/));
        
		ctx.close();
    }
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
