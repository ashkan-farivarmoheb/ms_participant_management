package com.tis.ms.participant.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.tis.ms.participant.repository.model.OrganisationType;
import com.tis.ms.participant.repository.model.cache.ProviderCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KryoSerializer implements Serializer<Object>, Deserializer<Object>{

	private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
		Kryo kryo = new Kryo();
		registerKryo(kryo);
		return kryo;
	});
	
	private static void registerKryo(Kryo kryo) {
		kryo.register(ProviderCache.class);
		kryo.register(OrganisationType.class);
	}
	
	@Override
	public Object deserialize(InputStream inputStream) throws IOException {
		try (Input input = new Input(new InflaterInputStream(inputStream))) {
			return kryos.get().readClassAndObject(input);
			
		} catch (Exception ex) {
			Log.error("message=\"Error when deserialize\", error_message=\"{}\"", ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void serialize(Object object, OutputStream outputStream) throws IOException {
		try (Output output = new Output(new DeflaterOutputStream(outputStream))) {
			kryos.get().writeClassAndObject(output, object);			
		} catch (Exception ex) {
			Log.error("message=\"Error when serialize\", error_message=\"{}\"", ex.getMessage());
			throw ex;
		}
	}

}
