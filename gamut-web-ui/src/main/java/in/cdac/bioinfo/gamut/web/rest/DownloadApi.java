package in.cdac.bioinfo.gamut.web.rest;

import java.awt.print.Book;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpBean;
import in.cdac.bioinfo.gamut.web.snp.SNPHomeBean;

@Path("/download")
public class DownloadApi {
	
	@Inject
	private SNPHomeBean snpHomeBean;
	
	
	@GET
    @Path("/txt")
	@Produces({ "text/plain" })
    public List<OutputSnpBean> txtFormat() {
		String path = System.getProperty("user.home")+File.separator+"download";
		File fileDownload = new File(path+File.separator+"myFile.txt");
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(fileDownload);
			
			for (int i = 0; i < snpHomeBean.getStrList().size(); i++) {
				fileWriter.write(snpHomeBean.getStrList().get(i).toString());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ResponseBuilder response = Response.ok((Object)fileDownload);
		
		response.header("Content-Disposition", "attachment;filename="+fileDownload);
		
		return snpHomeBean.getStrList();
    
    }

}
