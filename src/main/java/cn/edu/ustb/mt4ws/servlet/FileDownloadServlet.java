package cn.edu.ustb.mt4ws.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import cn.edu.ustb.mt4ws.configuration.Project;

import java.io.*;
import java.util.*;

public class FileDownloadServlet  extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=GBK";

    //Initialize global variables
    public void init() throws ServletException {
    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        response.setContentType(CONTENT_TYPE);

		HttpSession session = request.getSession(false);
        Project project = (Project) session.getAttribute("project");
        //得到下载文件的名字
        //String filename=request.getParameter("filename");
        
        //创建file对象
        File file=new File(project.getWorkspace().getPath() + "mt.log");//下载的日志文件名

        //设置response的编码方式
        response.setContentType("application/x-msdownload");

        //写明要下载的文件的大小
        response.setContentLength((int)file.length());

        //设置附加文件名
       // response.setHeader("Content-Disposition","attachment;filename="+filename);
        
        //解决中文乱码
    response.setHeader("Content-Disposition","attachment; filename="+project.getWorkspace().getPath() + "mt.log");       

        //读出文件到i/o流
        FileInputStream fis=new FileInputStream(file);
        BufferedInputStream buff=new BufferedInputStream(fis);

        byte [] b=new byte[1024];//相当于我们的缓存

        long k=0;//该值用于计算当前实际下载了多少字节

        //从response对象中得到输出流,准备下载

        OutputStream myout=response.getOutputStream();

        //开始循环下载

        while(k<file.length()){

            int j=buff.read(b,0,1024);
            k+=j;

            //将b中的数据写到客户端的内存
            myout.write(b,0,j);

        }

        //将写入到客户端的内存的数据,刷新到磁盘
        myout.flush();
        System.out.println("下载完成");

    }

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    }

}
