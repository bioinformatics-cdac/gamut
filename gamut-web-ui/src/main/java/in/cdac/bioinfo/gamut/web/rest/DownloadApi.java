package in.cdac.bioinfo.gamut.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpBean;
import in.cdac.bioinfo.gamut.web.snp.SNPHomeBean;

@Path("/download")
public class DownloadApi {

	@Inject
	private SNPHomeBean snpHomeBean;

	@GET
	@Path("/csv")
	@Produces({ "text/plain" })
	public Response txtFormat() {

		StringBuilder sb = new StringBuilder();

		sb.append("Chromosome,Position,Ref,SetOne,SetTwo,GeneList\n");
		for (int i = 0; i < snpHomeBean.getStrList().size(); i++) {
			sb.append(snpHomeBean.getStrList().get(i).csvFormat() + "\n");
		}

		ResponseBuilder response = Response.ok(sb.toString());

		response.header("Content-Disposition", "attachment;filename=download.csv");

		return response.build();

	}
	
	@GET
	@Path("/tsv")
	@Produces({ "text/plain" })
	public Response tsvFormat() {

		StringBuilder sb = new StringBuilder();

		sb.append("Chromosome\tPosition\tRef\tSetOne\tSetTwo\tGeneList\n");
		for (int i = 0; i < snpHomeBean.getStrList().size(); i++) {
			sb.append(snpHomeBean.getStrList().get(i).tsvFormat() + "\n");
		}

		ResponseBuilder response = Response.ok(sb.toString());

		response.header("Content-Disposition", "attachment;filename=download.tsv");

		return response.build();

	}
	
	@GET
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	public List<OutputSnpBean> jsonFormat() {		

		return snpHomeBean.getStrList();

	}
	

}
