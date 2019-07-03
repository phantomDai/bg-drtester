package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestMessagePrinter extends HttpServlet {
	volatile boolean fisrt = false;
	List<String> consoleMessageList = new Vector<String>();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(true);

			if (fisrt == false) {
				LoopedStreams catcher;
				try {
					catcher = new LoopedStreams();
					catcher.startByteArrayReaderThread();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			fisrt = true;
		
		String msg = request.getParameter("msg"); // 后台用getParameter得到ajax中传输的值，双引号里为ajax中值的名称。
		try {
			PrintWriter out = response.getWriter();// 定义一个 PrintWriter用来往ajax中写值。
			if (msg != null) {
				if (msg.equals("clear"))
					consoleMessageList.clear();
				if (msg.equals("refresh")) {
					for (int i = 0; i < consoleMessageList.size(); i++)
						out.println(consoleMessageList.get(i));
				}
			}
		} catch (IOException e) {
			;
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Catch Message from System Console
	 * 
	 * @author WangGuan
	 * 
	 */
	public class LoopedStreams {
		private PipedOutputStream pipedOS = new PipedOutputStream();
		private boolean keepRunning = true;
		private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
			public void close() {
				keepRunning = false;
				try {
					super.close();
					pipedOS.close();
				} catch (IOException e) {
					// 记录错误或其他处理
					// 为简单计，此处我们直接结束
					System.exit(1);
				}
			}
		};
		private PipedInputStream pipedIS = new PipedInputStream() {
			public void close() {
				keepRunning = false;
				try {
					super.close();
				} catch (IOException e) {
					// 记录错误或其他处理
					// 为简单计，此处我们直接结束
					System.exit(1);
				}
			}
		};

		public LoopedStreams() throws IOException {
			pipedOS.connect(pipedIS);
			startByteArrayReaderThread();
		} // LoopedStreams()

		public InputStream getInputStream() {
			return pipedIS;
		} // getInputStream()

		public OutputStream getOutputStream() {
			return byteArrayOS;
		} // getOutputStream()

		private void startByteArrayReaderThread() {
			new Thread(new Runnable() {
				public void run() {
					while (keepRunning) {
						// 检查流里面的字节数
						if (byteArrayOS.size() > 0) {
							byte[] buffer = null;
							synchronized (byteArrayOS) {
								buffer = byteArrayOS.toByteArray();
								byteArrayOS.reset(); // 清除缓冲区
							}
							try {
								// 把提取到的数据发送给PipedOutputStream
								 pipedOS.write(buffer, 0, buffer.length);
								consoleMessageList.add(new String(buffer));
							} catch (Exception e) {
								// 记录错误或其他处理
								// 为简单计，此处我们直接结束
								System.exit(1);
							}
						} else
							// 没有数据可用，线程进入睡眠状态
							try {
								// 每隔1秒查看ByteArrayOutputStream检查新数据
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
					}
				}
			}).start();
		} // startByteArrayReaderThread()
	} // LoopedStreams

}
