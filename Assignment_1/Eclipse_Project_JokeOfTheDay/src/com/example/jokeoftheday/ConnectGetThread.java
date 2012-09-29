package com.example.jokeoftheday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * @author Sagar Abhang and Aniket Pansare
 *
 */
public class ConnectGetThread extends MainActivity implements Runnable 
{
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		Socket socket = null;
		BufferedReader buffered_reader = null;
		PrintWriter printer_writer = null;
		
    	try 
    	{
    		//Create the new socket by specifying host and port 
			socket = new Socket("impact.asu.edu", 8000);
			buffered_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			Thread.sleep(100);
			StringBuffer msg_from_server = new StringBuffer("");
			
			//Start Handshake. Read HELLO from server
			//Read without blocking
			while(buffered_reader.ready()) 
			{
				msg_from_server.append((char) buffered_reader.read() );
			}
			
			Log.d("Verify msg_from_server: ", msg_from_server.toString());
			
			String server_name = msg_from_server.substring(
					"<AA1 HELLO THIS IS ".length() , msg_from_server.length() - 1);
			
			//Separate out the server name
			server_name = server_name.substring(0, server_name.indexOf(" "));
			
			//Verify the HELLO message received from server 
			if(msg_from_server.toString().contains("<AA1 HELLO THIS IS " + server_name + " WHO ARE YOU ") && 
					msg_from_server.toString().lastIndexOf(">") == (msg_from_server.length() - 1))
			{
				printer_writer = new PrintWriter(socket.getOutputStream(),true);
				
				//Send HELLO to server
				String msg_to_server = "<AA1 HELLO THIS IS Abhang_1205139178_Pansare_1205138918>";
				printer_writer.println(msg_to_server);
				
				Log.d("Verify msg_to_server: ", msg_to_server);
				
				Thread.sleep(100);
				
				msg_from_server = new StringBuffer("");
			
				//Read HI from server
				while(buffered_reader.ready()) 
				{
					msg_from_server.append((char) buffered_reader.read() );
				}
				
				Log.d("Verify msg_from_server: ", msg_from_server.toString());
				
				//Verify the HI message received from server
				if(msg_from_server.toString().contains("<AA1 HI ") && 
						msg_from_server.toString().lastIndexOf(">") == (msg_from_server.length() - 1))
				{
					//Send REQUEST to Server for getting joke of the day
					msg_to_server = "<AA1 REQUEST " + server_name + " JOTD>";
					printer_writer.println(msg_to_server);
				
					Log.d("Verify msg_to_server: ", msg_to_server);
					
					Thread.sleep(100);
					
					msg_from_server = new StringBuffer("");
					
					//Read RESPONSE from server
					while(buffered_reader.ready()) 
					{
						msg_from_server.append((char) buffered_reader.read() );
					}
					
					Log.d("Verify msg_from_server: ", msg_from_server.toString());
					
					joke = msg_from_server.toString();
					joke = joke.substring((joke.indexOf("JOTD") + "JOTD".length() + 1), joke.lastIndexOf(">"));
					
					//Verify the RESPONSE received from Server
					if(msg_from_server.toString().contains("<AA1 RESPONSE ") && 
							msg_from_server.toString().lastIndexOf(">") == (msg_from_server.length() - 1))
					{
						//Send GOODBYE to server
						msg_to_server = "<AA1 GOODBYE " + server_name + " >";
						printer_writer.println(msg_to_server);
					
						Log.d("Verify msg_to_server: ", msg_to_server);
						
						Thread.sleep(100);
						
						msg_from_server = new StringBuffer("");
						
						while(buffered_reader.ready()) 
						{
							msg_from_server.append((char) buffered_reader.read() );
						}
						
						Log.d("Verify msg_from_server: ", msg_from_server.toString());
						
						//Verify the GOODBYE received from server
						if(msg_from_server.toString().contains("<AA1 GOODBYE ") && 
								msg_from_server.toString().lastIndexOf(">") == (msg_from_server.length() - 1))
						{
							Log.d("Success: ", "Successfully received joke from Server.");
						}
					}
					else
					{
						Log.d("Error: RESPONSE MSG: ", "Unexpected RESPONSE from joke server...");
						joke = "Expected Error: Joke can not be displayed at this time.";
					}
				}
				else 
				{
					Log.d("Error: HI MSG: ", "Unexpected response to HI message from joke server...");
					joke = "Expected Error: Joke can not be displayed at this time.";
				}
			}
			else 
			{
				Log.d("Error: HELLO MSG: ", "Unexpected response to HELLO from joke server...");
				joke = "Expected Error: Joke can not be displayed at this time.";
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	finally
		{
    		//Close inputstream, outputstream and socket.
    		try 
    		{
    			if(buffered_reader != null)
    			{
    				buffered_reader.close();
    			}
    			if(printer_writer != null)
    			{
    				printer_writer.close();
    			}
    			if(socket != null)
    			{
    				socket.close();
    			}
    		}
    		catch(IOException ioexp)
    		{
    			Log.d("Error: ", "Problem in closing buffered_reader or printer_writer or socket");
    		}
		}
    }
}
