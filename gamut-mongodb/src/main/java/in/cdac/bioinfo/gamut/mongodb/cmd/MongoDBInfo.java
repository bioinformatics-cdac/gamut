/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.mongodb.cmd;

import com.beust.jcommander.Parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ramki
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoDBInfo {
	@Parameter(names = { "--host", "-h"}, description = "MogoDB Server IP address")
	private String host = "127.0.0.1";

	@Parameter(names = { "--port", "-p" }, description = "MogoDB Server Port")
	private int port = 27017;

	@Parameter(names = { "--database", "-d" }, description = "MogoDB Database Name")
	private String database = "gamut1";

	@Parameter(names = { "--collection", "-c" }, description = "MogoDB Collection/Table Name")
	private String collection = "humangnome";

	@Parameter(names = { "--genetable", "-g" }, description = "MogoDB Collection/Gene Table")
	private String genecollection = "genetable";

}
