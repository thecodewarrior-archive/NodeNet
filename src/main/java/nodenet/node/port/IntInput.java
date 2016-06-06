package nodenet.node.port;

import io.netty.buffer.ByteBuf;
import nodenet.node.net.InputPort;

public class IntInput extends InputPort<Integer> {

	public IntInput(int value) {
		super(Integer.class, value);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		setValue(buf.readInt());
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(getValue());
	}

}
