/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.spark.cmd;

import java.io.Serializable;

import com.beust.jcommander.Parameter;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ramki
 */

@Data
@NoArgsConstructor
public class MongoDBInfo implements Serializable{
	@Parameter(names = { "--host", "-h" }, description = "MogoDB Server IP address")
	private String host = "localhost";

	@Parameter(names = { "--port", "-p" }, description = "MogoDB Server Port")
	private int port = 27017;

	@Parameter(names = { "--database", "-d" }, description = "MogoDB Database Name")
	private String database = "gamut";

	@Parameter(names = { "--collection", "-c" }, description = "MogoDB Collection/Table Name")
	private String collection = "human";

	/*
	 * @Parameter(names = { "--genetable", "-c" }, description =
	 * "MogoDB Collection/Gene Table") private String genecollection = "genetable";
	 */

	
}
