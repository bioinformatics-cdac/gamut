/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.web.config;

import java.io.File;

/**
 *
 * @author Application
 */
public class PathUtil {

    String currentUsersHomeDir = System.getProperty("user.home");
    
    private static String application_home;

    public PathUtil() throws ApplicationConfigurationException {
        if (application_home == null) {
            application_home = currentUsersHomeDir+File.separatorChar+"gamut";
        }
    }

    public File getApplicationHomeDirectory() {
        System.out.println("Application home varicble --- "+application_home);
        File ApplicationHomeDirectory = new File(application_home);
        return ApplicationHomeDirectory;

    }

    public File getApplicationUserDirectory() {
        String ApplicationUserDirectoryPath = this.getApplicationHomeDirectory().getAbsolutePath() + File.separator + "user";
        File ApplicationUserDirectory = new File(ApplicationUserDirectoryPath);
        return ApplicationUserDirectory;
    }
    
    public File getApplicationUploadDocsDirectory() {
        String ApplicationUploadDirectoryPath = this.getApplicationHomeDirectory().getAbsolutePath() + File.separator + "docs";
        File ApplicationUploadrDirectory = new File(ApplicationUploadDirectoryPath);
        return ApplicationUploadrDirectory;
    }
    
    public File getApplicationUserUploadDirectory() {
        String ApplicationUserUploadDirectoryPath = this.getApplicationUserDirectory().getAbsolutePath() + File.separator + "upload";
        File ApplicationUseUploadrDirectory = new File(ApplicationUserUploadDirectoryPath);
        return ApplicationUseUploadrDirectory;
    }
    
    public File getApplicationRevokedDirectory() {
        String ApplicationUserRevokedDirectoryPath = this.getApplicationUserDirectory().getAbsolutePath() + File.separator+ "upload" +File.separator+ "revoked";
        File ApplicationUserRevokedDirectory = new File(ApplicationUserRevokedDirectoryPath);
        return ApplicationUserRevokedDirectory;
    }
    
     public File getApplicationUserPublicationDirectory() {
        String ApplicationUserPublicationDirectoryPath = this.getApplicationUserDirectory().getAbsolutePath() + File.separator + "publication";
        File ApplicationUserPublicationDirectory = new File(ApplicationUserPublicationDirectoryPath);
        return ApplicationUserPublicationDirectory;
    }
     
     public File getApplicationUserTestimonialDirectory() {
        String ApplicationUserTestimonialDirectoryPath = this.getApplicationUserDirectory().getAbsolutePath() + File.separator + "Testimonial";
        File ApplicationUserTestimonialDirectory = new File(ApplicationUserTestimonialDirectoryPath);
        return ApplicationUserTestimonialDirectory;
    }
     
     public File getApplicationVisitDirectory() {
        String ApplicationUserVisitDirectoryPath = this.getApplicationHomeDirectory() + File.separator + "visits";
        File ApplicationUserVisitDirectory = new File(ApplicationUserVisitDirectoryPath);
        return ApplicationUserVisitDirectory;
    }
     
    public File getApplicationProfilePicDirectory() {
        String ApplicationUserProfilePicDirectoryPath = this.getApplicationUserDirectory().getAbsolutePath() + File.separator + "profilepic";
        File ApplicationUserProfilePicDirectory = new File(ApplicationUserProfilePicDirectoryPath);
        return ApplicationUserProfilePicDirectory;
    }


    public File getApplicationTempDirectory() {
        String ApplicationBinDirectoryPath = this.getApplicationHomeDirectory().getAbsolutePath() + File.separator + "temp";
        File ApplicationBinDirectory = new File(ApplicationBinDirectoryPath);
        return ApplicationBinDirectory;
    }

    public File getApplicationBinDirectory() {
        String ApplicationBinDirectoryPath = this.getApplicationHomeDirectory().getAbsolutePath() + File.separator + "bin";
        File ApplicationBinDirectory = new File(ApplicationBinDirectoryPath);
        return ApplicationBinDirectory;
    }

    public File getApplicationConfDirectory() {
        String ApplicationConfDirectoryPath = this.getApplicationHomeDirectory().getAbsolutePath() + File.separator + "conf";
        File ApplicationConfDirectory = new File(ApplicationConfDirectoryPath);
        return ApplicationConfDirectory;
    }
   
    public void createdirectory(){

    }

}