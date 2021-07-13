/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.mongodb.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ramki
 */
@Data
@NoArgsConstructor
//@Parameters(separators = "=", commandDescription = "Record fetched/retrieved from the repository")

public class MongodbQueryCommand {

	@ParametersDelegate
	private MongoDBInfo mongoDBInfo = new MongoDBInfo();

	@Parameter(names = { "--chromosome", "-ch" }, required = true, description = "Chromosome Name")
	private String chromosome = "1";

	@Parameter(names = { "--start", "-s" }, required = true, description = "Start Position (inclusive)")
	private long start = 1;

	@Parameter(names = { "--end", "-e" }, required = true, description = "End Position (inclusive")
	private long end = 1;

	@Parameter(names = { "--output", "-o" }, description = "OutPut File Name")
	private String output;

	@Parameter(names = "-left", required = true, description = "Left ")
	private List<String> left = new ArrayList<>();

	@Parameter(names = "-right", required = true, description = "Right ")
	private List<String> right = new ArrayList<>();

}
