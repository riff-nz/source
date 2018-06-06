package nz.riff.builder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.espr.injector.Configuration;
import it.espr.injector.Injector;

public class Main {

	public static final void main(String[] args) {
		Injector.injector(new Configuration() {

			@Override
			protected void configure() {
				super.configure();

				ObjectMapper json = new ObjectMapper();
				//json.enable(SerializationFeature.INDENT_OUTPUT);
				json.setSerializationInclusion(Include.NON_EMPTY);

				bind(json).named("json");
				bind(new ObjectMapper(new YAMLFactory())).named("yaml");
			}
		}).get(Builder.class).build();
	}
}
