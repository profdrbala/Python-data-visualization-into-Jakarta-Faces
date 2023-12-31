import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author bala
 */
@Named(value = "myBean")
@ApplicationScoped
public class MyBean {
private String instruction;
private String output;
private String picFile; //="resources//images//plot.jpg";
    public MyBean() {
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getPicFile() {
        return picFile;
    }

    public void setPicFile(String picFile) {
        this.picFile = picFile;
    }
    
    public void connect(){
        String output="";
        try{
            String sendData =getInstruction();
            setInstruction("");
            String[] code = sendData.split("\n");
            
            if (sendData.length() > 0) {
                Socket s=null;
                DataInputStream in=null;
                DataOutputStream out=null;
             for (String xcode : code ) {
                s=new Socket("10.153.70.91",1234); //10.153.70.91 192.168.0.106
                in=new DataInputStream(s.getInputStream());
                out=new DataOutputStream(s.getOutputStream());
                output = ">>> "+ xcode + "\n"; //+ result.getText();
                out.write(xcode.trim().getBytes());
                   
                   if(xcode.equals("exit()") || xcode.equals("quit()") ) {
                        s.close();
                        output += "Disconnected"+ "\n" + getOutput();
                    }
                    else{ //receive from python
                       byte[] bdata=new byte[64000];
                       String sdata="",edata="";
                       if(xcode.trim().equals("chart")){

                        FacesContext facesContext = FacesContext.getCurrentInstance();
                        String path = (String) facesContext
                                           .getExternalContext()
                                           .getRealPath("/");
                                   
                           File f=new File(path + "\\resources\\images\\plot.jpg");
                          
                           setOutput("Exist " + f.exists());

                           if(f.exists())  f.delete();
                           setOutput("Exist " + f.exists());
                           
                           FileOutputStream fileOutputStream = new FileOutputStream(path + "\\resources\\images\\plot.jpg");
                           fileOutputStream.write(bdata, 0, in.read(bdata,0,bdata.length));
                           fileOutputStream.close();
                           fileOutputStream.flush();
                           setOutput("Plot.jpg loaded");
                           //if(getPicFile().equals(""))   
                               setPicFile("plot.jpg");
                        }
                        else{
                            sdata=String.valueOf(in.read(bdata,0,bdata.length)); //numbers
                            edata = new String(bdata);                           //text                   
                        }
                   if(edata.trim().equals("")){
                        output += ">>> "+ sdata + "\n" + getOutput();
                        setOutput(output); // number data
                    }
                    else{
                        output += ">>> "+ edata + "\n" + getOutput();
                        setOutput(output);  //text data
                        if(edata.contains("Runtime Error: ")) {
                        output = "Connection Terminated, reconnect again..\n" + output; 
                        setOutput(output); }
                    }
                    }
               }
            }
            else {
                output += "Type the command to send to the Python server..." + "\n" + getOutput();
                setOutput(output);
            }           
         }catch(Exception e1){ output += "Runtime Error" + e1.toString() + "\n" + getOutput(); setOutput(output);}
    }
 }

