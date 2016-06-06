package nodenet.node.port;

import io.netty.buffer.ByteBuf;
import nodenet.node.NodeBase;
import nodenet.node.net.OutputPort;

public class IntOutput extends OutputPort<Integer> {

	public IntOutput(int value, NodeBase node) {
		super(Integer.class, value, node);
	}

	@Override
	public int getColor() {
		if(getValue() > 0) {
			return 0xFF0000;
		} else {
			return 0x7F0000;
		}
	}
	
	@Override
	public void readValueFromBuf(ByteBuf buf) {
		setValue( buf.readInt() );
	}

	@Override
	public void writeValueToBuf(ByteBuf buf) {
		buf.writeInt(getValue());
	}

}
