package digital.thinkport;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StatelessStream {

    public static void main(String[] args) {
        Properties properties =  getProperties();

        StreamsBuilder streamsBuilder = new StreamsBuilder();



        KStream<String,CallStatus> inputStream = streamsBuilder
                .stream("services.call.status.changed.0", Consumed.with(Serdes.String(), getCallStatusSerde(properties)));

        inputStream.peek((k,v)-> System.out.println(v))
                .filter((k,v)->v.getStatus()==Status.FAILED)
                .mapValues(v->new CallFailed(v.getTimestamp(),v.getParticipants(),"Original Request Failed."))
                .to("services.call.status.failed.0",Produced.with(Serdes.String(), getCallStatusFailed(properties)));

        inputStream.peek((k,v)-> System.out.println(v))
                .filter((k,v)->v.getParticipants().isEmpty())
                .mapValues(v->new CallFailed(v.getTimestamp(),v.getParticipants(),"Original Request Failed."))
                .to("services.call.status.failed.0",Produced.with(Serdes.String(), getCallStatusFailed(properties)));


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();


    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='W62L4UV5EWJ2D7A6' password='U5K4Rk9oUJJd2gZOs0mRjeET7pAv2eWGgoo8vgO/HrgU3gDtUX2qTMSyxr/IxgSw';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"call-analyzer-1");
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        //SCHEMA-REGISTRY#

        properties.put("schema.registry.url", "https://psrc-2312y.europe-west3.gcp.confluent.cloud");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "VWE36QWXLK3QXTCT:WGBX7QQWsTLD5sVBOY1O/kd8LSzFfEN31WXPM60VAfY2mFfghe4OKlpAeovvXb9K");
        return properties;
    }
    private static SpecificAvroSerde<CallStatus>  getCallStatusSerde(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<CallStarted>  getCallStatusStarted(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<CallEnded>  getCallStatusEnded(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<CallFailed>  getCallStatusFailed(Properties properties) {
        return getGenericSerde(properties);
    }

    private static <T extends SpecificRecord> SpecificAvroSerde<T> getGenericSerde(Properties properties){
        final Map<String, String> genericSerdeConfig = new HashMap<>();
        genericSerdeConfig.put("schema.registry.url", properties.getProperty("schema.registry.url"));
        genericSerdeConfig.put("basic.auth.credentials.source", properties.getProperty("basic.auth.credentials.source"));
        genericSerdeConfig.put("basic.auth.user.info", properties.getProperty("basic.auth.user.info"));
        final SpecificAvroSerde<T> genericSerde = new SpecificAvroSerde<>();
        genericSerde.configure(genericSerdeConfig, false); // `false` for record values
        return genericSerde;
    }


}