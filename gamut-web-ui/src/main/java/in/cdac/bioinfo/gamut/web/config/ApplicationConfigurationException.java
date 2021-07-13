/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package in.cdac.bioinfo.gamut.web.config;

/**
 *
 * @author mopac
 */
public class ApplicationConfigurationException extends Exception {

    public ApplicationConfigurationException(String message) {
        super(message);
        
    }

    ApplicationConfigurationException(String message,Exception exception) {
       super(message,exception);
    }

}
