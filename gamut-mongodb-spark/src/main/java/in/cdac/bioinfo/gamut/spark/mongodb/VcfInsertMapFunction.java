package in.cdac.bioinfo.gamut.spark.mongodb;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

import in.cdac.bioinfo.gamut.spark.cmd.MongoDBInfo;
import in.cdac.bioinfo.gamut.spark.vcf.bean.Vcf;
import in.cdac.bioinfo.gamut.spark.vcf.bean.VcfParser;

public class VcfInsertMapFunction implements MapFunction<String, Integer>, Serializable {

	private List<String> listOfHeaders;
	private VcfParser vcfParser;
	private MongoDBInfo mongoDBInfo;

	public VcfInsertMapFunction(List<String> listOfHeaders, MongoDBInfo mongoDBInfo) {
		this.listOfHeaders = listOfHeaders;
		this.vcfParser = new VcfParser(this.listOfHeaders);
		this.mongoDBInfo = mongoDBInfo;
	

	}

	@Override
	public Integer call(String line) throws Exception {
		if (!line.startsWith("#")) {
			Vcf vcfBean = vcfParser.processVCFLine(line);
			if (vcfBean != null) {
				if (vcfBean.getMapAltLines().size() > 0) {
					try {
						// System.out.println("in INSERT");
						MongoCollection<Document> collection = MongoDbClient.getCollection(mongoDBInfo);
						MongoDbClient.insert(vcfBean, collection);
					} catch (Throwable throwable1) {

						System.out.println("LINE_DUPLICATE=>" + vcfBean.getLineToProcess());
					}

				}

			}
		}
		return 1;

	}

}
