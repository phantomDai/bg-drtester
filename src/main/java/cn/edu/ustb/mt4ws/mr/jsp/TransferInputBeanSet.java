package cn.edu.ustb.mt4ws.mr.jsp;

import java.util.Vector;

public class TransferInputBeanSet {
	
    private Vector<TransferInputBean> transferset;

	public void setTransferset(Vector<TransferInputBean> transferset) {
		this.transferset = transferset;
	}

	public Vector<TransferInputBean> getTransferset() {
		return transferset;
	}
	
}
