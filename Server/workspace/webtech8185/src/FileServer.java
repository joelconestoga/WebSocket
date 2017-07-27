
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/mysocket")
public class FileServer {
    private static final String IMAGES_FOLDER = "C:/BKP/";
	
    static File newFile = null;
    static String fileName = null;
    static FileOutputStream fos = null;
    static boolean isLoggedIn;

    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        System.out.println("WEB SOCKET CREATED...");
    }

    @OnMessage
    public String receivingString(Session session, String message) {
    	
    	System.out.println("RECEIVED MESSAGE: " + message);

    	String response = "";
    	
    	String keyWord = getKeyword(message);   	

    	try {
        	switch (keyWord) {
        	
	    		case "login":
	    			response = authenticate(getValue(message));
	    			break;
	    			
	    		case "filename":
	    			response = createEmptyFile(getValue(message));
					break;
	    	
		    	case "eof":
		    		response = closeNewFile();
					break;
	
		    	case "listFiles":
		    		response = getUploadedFileNames();
		    		break;
		
				default:
	                response = "Uknown keyword: " + keyWord;
        	}
    		
    	} catch (Exception e) {
			response = e.getMessage();
		}
    	
    	return response;
    }

	@OnMessage
    public String receivingBytes(ByteBuffer bytes, boolean last, Session session) throws Exception {
    	
    	if (!isLoggedIn)
    		throw new Exception("> USER NOT AUTHENTICATED");

    	System.err.println("... RECEIVED BYTES");

    	String response = "";

    	while(bytes.hasRemaining()) {         
            try {
                fos.write(bytes.get());
                response = "\t...Server receiving bytes...";
            } catch (IOException e) {               
                e.printStackTrace();
                response = "Error while uploading file content.";
            }
        }
        
        return response;
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
		isLoggedIn = false;
    	System.out.println("CONNECTION CLOSED: " + reason.getReasonPhrase());
    }

    @OnError
    public void error(Session session, Throwable t) {
		isLoggedIn = false;
    	System.out.println("CONNECTION CLOSED: " + t.getMessage());
    }
    
    private String getKeyword(String message) {
    	int colonPosition = message.indexOf(':');
    	return colonPosition == -1 ? message : message.substring(0, colonPosition);
    }
    
    private String getValue(String message) {
    	return message.substring(message.indexOf(':') + 1);
    }
    
    private String createEmptyFile(String name) throws Exception {
        
    	if (!isLoggedIn)
    		throw new Exception("> USER NOT AUTHENTICATED");
    	
    	fileName = name;
    	
    	newFile = new File(IMAGES_FOLDER + fileName);
        fos = new FileOutputStream(newFile);
        return "> " + fileName + " created(empty)";    	
    }
    
    private String authenticate(String credentials) {
		
    	int sep = credentials.indexOf("SEPARATOR");
    	
    	String user = credentials.substring(0, sep);
    	String pass = credentials.substring(sep + 9);
    	
		if ("admin".equals(user) && "123".equals(pass)) {
			isLoggedIn = true;
			return "> LOGGED IN";
		} else {
			isLoggedIn = false;
			return "> INVALID USER/PASSWORD";
		}
	}

    private String closeNewFile() throws IOException {
    	fos.flush();
        fos.close();
        return "> " + fileName + " uploaded!";
    }
    
    private String getUploadedFileNames() {
    	String result = "> FILES ON SERVER:";
    	
		File[] files = new File(IMAGES_FOLDER).listFiles();
    	
		for (File file : files) {
			result += "\n\t - " + file.getName();
		}		
		
		return result;
	}    
}