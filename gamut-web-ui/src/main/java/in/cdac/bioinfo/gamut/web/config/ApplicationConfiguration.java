/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mopac
 */
public class ApplicationConfiguration {

    private static Properties applicationProperties = null;

    public static String getApplicationPropertiesPath() throws ApplicationConfigurationException {
        String applicationHome = null;

        String currentUsersHomeDir = System.getProperty("user.home");
        
        applicationHome = currentUsersHomeDir+File.separator+"gamut_home";
        
        System.out.println("USER HOME : "+currentUsersHomeDir);
        
        if (applicationHome == null) {
            throw new ApplicationConfigurationException("GAMUT_HOME Not Found in environment variable");
        }

        return applicationHome + File.separatorChar + "conf" + File.separatorChar + "application.properties";
    }

    static {
        try {
            initializeApplicationProperties();
        } catch (ApplicationConfigurationException ex) {
            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initializeApplicationProperties() throws ApplicationConfigurationException {

        try {

            applicationProperties = new Properties();
            String propertyFilePath = getApplicationPropertiesPath();
            File propertiesFile = new File(propertyFilePath);
            FileInputStream fis = new FileInputStream(propertiesFile);

            applicationProperties.load(fis);
            applicationProperties.list(System.out);

        } catch (Exception exception) {

            exception.printStackTrace();
            throw new ApplicationConfigurationException("Some problem in reading properties file", exception);
        }

    }

    public static String getApplicationHome() throws ApplicationConfigurationException {
        return applicationProperties.getProperty("APPLICATION_HOME");
    }

    public static String getHome() {
        return applicationProperties.getProperty("HOME");
    }

    public static String getWebApplicationName() {
        String webApplicationName = applicationProperties.getProperty("APPLICATION_WEBAPPLICATIONNAME");

        if (webApplicationName == null) {
            webApplicationName = "GAMUT";
        }

        return webApplicationName;
    }

//    public static String getPort() {
//        String port = applicationProperties.getProperty("APPLICATION_PORT");
//
//        if (port == null) {
//            port = "8080";
//        }
//        return port;
//    }


    
//    public static String getFacilityName() {
//        return applicationProperties.getProperty("FACILITY_NAME");
//    }
//    
//    
//
//
//    public static String getGenomicApplications() {
//        return applicationProperties.getProperty("GENOMIC_APPLICATIONS");
//    }
//    
//    public static String getMdApplications() {
//        return applicationProperties.getProperty("MD_APPLICATIONS");
//    }
     

    public static void main(String[] args) {
        System.out.println(applicationProperties.getProperty("APPLICATION_NUMBER_THREADS"));
    }

//    public static String getMailSmtpHost() {
//        String mailsmtphost = applicationProperties.getProperty("MAIL_SMTP_HOST");
//
//        if (mailsmtphost == null) {
//            mailsmtphost = "smtp.cdac.in";
//        }
//
//        return mailsmtphost;
//    }
//
//    public static String getSumHelpMailId() {
//        String helpemail = applicationProperties.getProperty("HELP_DESK_EMAIL");
//
//        if (helpemail == null) {
//            helpemail = "braf-help@cdac.in";
////            mailbrafhelpemail = "renu.gadhari@gmail.com";
//        }
//
//        return helpemail;
//    }
//    
//    public static String getAPIEndPoint(){
//        String apiEndPoint = applicationProperties.getProperty("API_ENDPOINT");
//
//        if (apiEndPoint == null) {
//            apiEndPoint = "http://biochrome.pune.cdac.in:8000";
////            mailbrafhelpemail = "renu.gadhari@gmail.com";
//        }
//
//        return apiEndPoint;
//        
//        
//    }
//
//    public static String getSumHelpMailPassword() {
//        String helpemailpassword = applicationProperties.getProperty("HELP_DESK_EMAIL_PASSWORD");
//
//        if (helpemailpassword == null) {
//            helpemailpassword = "$@jeshkumar";
////            mailbrafhelpemail = "renu.gadhari@gmail.com";
//        }
//
//        return helpemailpassword;
//    }

    
    static void saveProperties(Properties p) throws IOException {
        FileOutputStream fr;
        try {
            fr = new FileOutputStream(getApplicationPropertiesPath(),true);
            p.store(fr, null);
            fr.close();       
        } catch (ApplicationConfigurationException ex) {
            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//    public static void setSumHelpMailPassword(String HelpEmailPassword) {
//        applicationProperties.setProperty("HELP_DESK_EMAIL_PASSWORD", HelpEmailPassword);
//            try {
//                FileInputStream in = new FileInputStream(getApplicationPropertiesPath());
//                Properties props = new Properties();
//                props.load(in);
//                in.close();
//
//                File f=new File(getApplicationPropertiesPath());
//                System.out.println("Delete : "+f.delete());
//
//                FileOutputStream out = new FileOutputStream(getApplicationPropertiesPath());
//                props.setProperty("HELP_DESK_EMAIL_PASSWORD", HelpEmailPassword);
//                props.store(out, null);
//                out.close();           
//
//
//    //            saveProperties(applicationProperties);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            catch (ApplicationConfigurationException ex) {
//                 ex.printStackTrace();
//                Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//            }
//    }
//    public static void setSumGenomicApplicationList(String genomicApplicationList) {
//        applicationProperties.setProperty("GENOMIC_APPLICATIONS", genomicApplicationList);
//        
//         try {
//            FileInputStream in = new FileInputStream(getApplicationPropertiesPath());
//            Properties props = new Properties();
//            props.load(in);
//            in.close();
//
//            File f=new File(getApplicationPropertiesPath());
//            
//            FileOutputStream out = new FileOutputStream(getApplicationPropertiesPath());
//            props.setProperty("GENOMIC_APPLICATIONS", genomicApplicationList);
//            props.store(out, null);
//            out.close();           
//            
//            
////            saveProperties(applicationProperties);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        catch (ApplicationConfigurationException ex) {
//             ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//         
//    }
//    
//    public static void setSumMdApplicationList(String mdApplicationList) {
//    applicationProperties.setProperty("MD_APPLICATIONS", mdApplicationList);
//        
//         try {
//            FileInputStream in = new FileInputStream(getApplicationPropertiesPath());
//            Properties props = new Properties();
//            props.load(in);
//            in.close();
//
//            File f=new File(getApplicationPropertiesPath());
//            FileOutputStream out = new FileOutputStream(getApplicationPropertiesPath());
//            props.setProperty("MD_APPLICATIONS", mdApplicationList);
//            props.store(out, null);
//            out.close();           
//            
////            saveProperties(applicationProperties);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        catch (ApplicationConfigurationException ex) {
//             ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public static void setSumHelpMailId(String HelpEmailId) {
//
//        applicationProperties.setProperty("HELP_DESK_EMAIL", HelpEmailId);
//        try {
//            FileInputStream in = new FileInputStream(getApplicationPropertiesPath());
//            Properties props = new Properties();
//            props.load(in);
//            in.close();
//
//            File f=new File(getApplicationPropertiesPath());
//            System.out.println("Delete : "+f.delete());
//            
//            FileOutputStream out = new FileOutputStream(getApplicationPropertiesPath());
//            props.setProperty("HELP_DESK_EMAIL", HelpEmailId);
//            props.store(out, null);
//            out.close();           
//            
//            
////            saveProperties(applicationProperties);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        catch (ApplicationConfigurationException ex) {
//             ex.printStackTrace();
//            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }


//    public static String getPathRegPDF() {
//        String pdfpath = applicationProperties.getProperty("BRAF_REGISTRATION_PDF");
//
//        return pdfpath;
//    }

    public static void refresh(){
      try {
            initializeApplicationProperties();
        } catch (ApplicationConfigurationException ex) {
            Logger.getLogger(ApplicationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
