import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.File;
import java.util.*;

 class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;
  OutputStream os = null;
  FileOutputStream fos = null;
  InputStream is = null;//stream read from the socket
  BufferedOutputStream bos = null;
	String message;                //message send to the server
	String MESSAGE;
  FileInputStream fis = null;
  boolean auth = false;

BufferedInputStream bis = null;
	public void Client() {}

	void run() throws ClassNotFoundException
	{
      Scanner scan = new Scanner(System.in);
      boolean auth = false;
      String userpass;

		try{
			//create a socket to connect to the server
        requestSocket = new Socket("localhost", 8000);

         while(!auth)
         {
           System.out.println("Enter the username and password");
            userpass = scan.nextLine();
         if(userpass.equals("neharika khera"))
         {
           System.out.println("Logged in successfully!!");
           System.out.println("Connected to localhost 8000");
           auth = true;
           break;
         }
         else
         System.out.println("Wrong username password, Try Again!!");
         }
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
                      //  Scanner scan = new Scanner(System.in);
			//get Input from standard input
			//BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
          int count, current = 0;
          System.out.print("Enter the ftp command:");
          message = scan.nextLine();
          String[] command = message.split(" ");
        switch(command[0])
        {
          case "ftpclient":
          {
            System.out.println("You are already logged in!!");
            break;
          }
          case"dir":
          {
            sendMessage(message);
            MESSAGE = (String)in.readObject();
            System.out.println(MESSAGE);

            break;
          }
           case "get":
           {
             try {
             sendMessage(message);
             String fileFound = (String)in.readObject();
            // System.out.println(fileFound);
             if(fileFound.equals("File not found")) {
               System.out.println("File not found");
               break;
             }
             is = requestSocket.getInputStream();
             fos = new FileOutputStream("C:/Users/nehar/desktop/CN2/Client1/"+ command[1]);
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
             System.out.println("File " +command[1]+ "recieved");
           }
           finally
           {
             if(fos != null) fos.close();
             if (bos != null) bos.close();
           }
             break;

           }


           case "upload":
           {
             File file;
             try {
             try{
                file = new File(command[1]);
               fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
               out.writeObject("upload "+ command[1]);
               out.flush();
             }
             catch(FileNotFoundException err) {
               System.out.println("File not exist");
               break;
             }

             os = requestSocket.getOutputStream();
             byte[] mybytes = new byte[(int)file.length()];
             bis.read(mybytes, 0, mybytes.length);
             System.out.println("Uploading " +command[1]);
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
           default:
           {
             System.out.println("Invalid command");
           }

        }
        //read a sentence from the standard input
			//	  message = bufferedReader.readLine();
				//Send the sentence to the server
			  //     sendMessage(message);
				//Receive the upperCase sentence from the server
			//	MESSAGE = (String)in.readObject();
				//show the message to the user
				//System.out.println("Receive message: " + MESSAGE);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//main method
	public static void main(String args[])throws ClassNotFoundException
	{
    String message;
    String userpass;
    Scanner scan = new Scanner(System.in);
    while(true){
    System.out.print("Enter the ftp command: ");
      message = scan.nextLine();
      String[] command = message.split(" ");

       switch(command[0])
       {
        case "ftpclient":
         {
           //System.out.println(command[1]);
         if((command[1].equals("localhost") || command[1].equals("127.0.0.1")) && command[2].equals("8000"))
          {
              Client client = new Client();
               client.run();
          }
         else
             System.out.println("Invalid ip or port number");
          break;
          }
          default:
          {
            System.out.println("Please login first!!");

          }
          break;
       }
     }


}






}
