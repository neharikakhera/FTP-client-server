import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

 class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
        	ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
        	try {
            		while(true) {
                		new Handler(listener.accept(),clientNum).start();
				System.out.println("Client "  + clientNum + " is connected!");
				clientNum++;
            			}
        	} finally {
            		listener.close();
        	}

    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
      private String message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
        	private ObjectInputStream in;	//stream read from the socket
        	private ObjectOutputStream out;    //stream write to the socket
          FileInputStream fis = null;
          BufferedInputStream bis = null;
            OutputStream os = null;
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            InputStream is = null;
		private int no;		//The index number of the client
    int count, current=0;

        	public Handler(Socket connection, int no) {
            		this.connection = connection;
	    		this.no = no;
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
   //   os = sock.getOutputStream();
			try{
				while(true)
				{
					//receive the message sent from the client
					message = (String)in.readObject();
          String[] command = message.split(" ");
          switch(command[0])
          {
              case "dir":
              {
              String curr_dir = new java.io.File(".").getCanonicalPath();
              File folder = new File(curr_dir);
                File[] filelist = folder.listFiles();
                String filenames="";
                for(File file: filelist)
                {
                  filenames+= " # " +file.getName();
                }
                 sendMessage(filenames);

                break;
                }
          case "get":
          {
            File file;
            try {
            try{
              file = new File(command[1]);
              fis = new FileInputStream(file);
               bis = new BufferedInputStream(fis);
              out.writeObject("File found");
            }
            catch(FileNotFoundException err) {
              out.writeObject("File not found");
              break;
            }

            os = connection.getOutputStream();
            byte[] mybytes = new byte[(int)file.length()];
            bis.read(mybytes, 0, mybytes.length);
            System.out.println("Sending " +command[1]);
            os.write(mybytes, 0, mybytes.length);
            os.flush();
            System.out.println("Done");
          }
          finally{
            if (bis != null) bis.close();
          if (os != null) os.close();
           }


            break;
          }
          case "upload":
          {
            try
            {
            is = connection.getInputStream();
            fos = new FileOutputStream("C:/Users/nehar/desktop/CN2/Server/"+ command[1]);
             bos = new BufferedOutputStream(fos);
            byte[] mybytes = new byte[1024*1024];
            count = is.read(mybytes, 0, mybytes.length);
            current = count;
            do {
               count =
                  is.read(mybytes, current, (mybytes.length-current));
               if(count >= 0) current += count;
            } while(count > -1);
            bos.write(mybytes,0,current);
            bos.flush();
            System.out.println("File " +command[1]+ " recieved");
          }
          finally
          {
            if(fos != null) fos.close();
            if (bos != null) bos.close();
          }


            break;
          }
        }
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Client " + no);
		}
		finally
    {
			//Close connections
			try{
				in.close();
				out.close();
				connection.close();
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
			}
		}
	}

	//send a message to the output stream
	public void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg + " to Client " + no);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

    }

}
