package digital.thinkport;

import net.datafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicProducer {
    public static void main(String[] args) {
        Properties properties =  getProperties();
        Faker faker = new Faker();
        String callId = String.valueOf(faker.idNumber());
        String customer = faker.rickAndMorty().character();

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        String callEvent = "CallId:" + callId + ";Customer:" + customer + ";Event: Call started";
        producer.send(new ProducerRecord<>("services.call.status.0",callEvent));

        producer.flush();
        producer.close();
    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='MTHMXNOJJOMJDWKC' password='E5AL3BwH3tvuz7nnZyc4T/ENN2TC0UUNTOces8gPefP2jtL+G5HRE8hjgI1bpFgJ';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return properties;
    }
}