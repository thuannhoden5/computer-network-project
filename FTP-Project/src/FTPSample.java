import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
public class FTPSample {
    private static String server;
    private static  int port ;
    private static final int timeout = 60000;
    private static final int BUFFER_SIZE = 1024 * 1024 * 1;
    private static String username ;
    private static String password ;
    private static FTPClient ftp;
 

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args) {
    	
       connectServer();
       boolean con = true;
       while(con) {
    	   System.out.println("Menu");
    	   System.out.println("1. Create new directory");
    	   System.out.println("2. Upload file");
    	   System.out.println("3. Upload directory");
    	   System.out.println("4. Download file from remote");
    	   System.out.println("5. Show list file from remote");
    	   System.out.println("6. Exit");
    	   Scanner sc = new Scanner(System.in);
    	   int a = sc.nextInt();
    	   
    	   switch(a) {
    	   case 1:
    		   createDir("");
    		   break;
    	   case 2:
    		   Scanner sc2 = new Scanner(System.in);
    		    System.out.println("Enter local file path");
    		    String localFilePath = sc2.nextLine();
    		    System.out.println("Enter remote file path");
    		    String remoteFilePath = sc2.nextLine(); 
    		   
			try {
				uploadSingleFile(localFilePath, remoteFilePath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
    		   break;
    	   case 3:
    		   Scanner sc1 = new Scanner(System.in);
    		     System.out.println("Enter remote directory path");
    		    String remoteDirPath = sc1.nextLine();
    		     System.out.println("Enter local parent dir path");
    		     String localParentDir = sc1.nextLine();
    		     System.out.println("Enter name new directory");
    		     String newDir = sc.nextLine();
    		   try {
				uploadDirectory(remoteDirPath, localParentDir,newDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		   break;
    	   case 4:
    		  
    		   downloadFile("", "");
    		   break;
    	   case 5:
    		   printFileDetails();
    		   break;
    	   case 6:
    		   con = false;
    		   break;
    	   }
    	   
       }
//       createDir("/up1");
//       try {
//		uploadDirectory("/up1","/Users/nguyenthuan/Downloads/Dijkstra","");
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//       try {
//		System.out.println(uploadSingleFile("/Users/nguyenthuan/Documents/201872777.docx","/upload20187277.docx"));
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    // Lists files and directories
//       FTPFile[] files1 = null;
//	try {
//		files1 = ftp.listFiles("/up1");
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//       printFileDetails(files1);

    }
 
    public static void connectServer() {
    	ftp = new FTPClient();
    	try {
    		
    		Scanner sc2 = new Scanner(System.in);
    		System.out.println("Enter host");
    		server = sc2.nextLine();
    		System.out.println("Enter port");
    		String port1 = sc2.nextLine();
    		port = Integer.parseInt(port1);
            ftp.connect(server, port);
            showServerReply(ftp);
            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            System.out.println("Enter username");
            username = sc2.nextLine();
            System.out.println("Enter password");
            password = sc2.nextLine();
            
            
            boolean success = ftp.login(username, password);
            showServerReply(ftp);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER");
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
    }
    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftp.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
    private static void createDir(String newDir) {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter new directory");
    	newDir = sc.nextLine();
       	String dirToCreate = newDir;
        boolean success = false;
		try {
			success = ftp.makeDirectory(dirToCreate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        showServerReply(ftp);
        if (success) {
            System.out.println("Successfully created directory: " + dirToCreate);
        } else {
            System.out.println("Failed to create directory. See server's reply.");
        }
        
    }
    public static boolean uploadSingleFile(String localFilePath, String remoteFilePath
         ) throws IOException {
    
    		 File localFile = new File(localFilePath);
        InputStream inputStream = new FileInputStream(localFile);
        try {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            System.out.println("Upload file success");
            return ftp.storeFile(remoteFilePath, inputStream);
        } finally {
            inputStream.close();
        }
    }
    public static void uploadDirectory(String remoteDirPath, String localParentDir,
              String remoteParentDir)
            throws IOException {
     
     
        System.out.println("LISTING directory: " + localParentDir);
     
        File localDir = new File(localParentDir);
        File[] subFiles = localDir.listFiles();
        if (subFiles != null && subFiles.length > 0) {
            for (File item : subFiles) {
                String remoteFilePath = remoteDirPath + "/" + remoteParentDir
                        + "/" + item.getName();
                if (remoteParentDir.equals("")) {
                    remoteFilePath = remoteDirPath + "/" + item.getName();
                }
     
     
                if (item.isFile()) {
                    // upload the file
                    String localFilePath = item.getAbsolutePath();
                    System.out.println("About to upload the file: " + localFilePath);
                    boolean uploaded = uploadSingleFile(
                            localFilePath, remoteFilePath);
                    if (uploaded) {
                        System.out.println("UPLOADED a file to: "
                                + remoteFilePath);
                    } else {
                        System.out.println("COULD NOT upload the file: "
                                + localFilePath);
                    }
                } else {
                    // create directory on the server
                    boolean created = ftp.makeDirectory(remoteFilePath);
                    if (created) {
                        System.out.println("CREATED the directory: "
                                + remoteFilePath);
                    } else {
                        System.out.println("COULD NOT create the directory: "
                                + remoteFilePath);
                    }
     
                    // upload the sub directory
                    String parent = remoteParentDir + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        parent = item.getName();
                    }
     
                    localParentDir = item.getAbsolutePath();
                    uploadDirectory(remoteDirPath, localParentDir,
                            parent);
                }
            }
        }
    }
    public static void downloadFile(String remoteFile, String downloadFile) {
    	// APPROACH #1: using retrieveFile(String, OutputStream)
    	Scanner sc = new Scanner (System.in);
    	System.out.println("Enter remote file path");
    	remoteFile = sc.nextLine();
    	System.out.println("Enter download file path");
    	downloadFile = sc.nextLine();
        String remoteFile1 = remoteFile;
        File downloadFile1 = new File(downloadFile);
        OutputStream outputStream1 = null;
		try {
			outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        boolean success = false;
		try {
			success = ftp.retrieveFile(remoteFile1, outputStream1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			outputStream1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (success) {
            System.out.println("File has been downloaded successfully.");
        }
    }
    private static void printFileDetails() {
    	FTPFile[] files = null;
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter remote directory path");
    	String lf = sc.nextLine();
		try {
			files = ftp.listFiles(lf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (FTPFile file : files) {
            String details = file.getName();
            if (file.isDirectory()) {
                details = "[" + details + "]";
            }
            details += "\t\t" + file.getSize();
            details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
 
            System.out.println(details);
        }
    }
    
 
}