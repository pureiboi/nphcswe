package com.nphc.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.nphc.helper.exception.BadInputException;

public class CsvReader {

	private CsvReader() {
	}

	public static <T> List<T> toList(Class<T> clazz, byte[] data) {

		CsvMapper csvMapper = new CsvMapper();
		CsvSchema csvSchema = csvMapper.typedSchemaFor(clazz).withHeader();
		
		List<T> resulttList = new ArrayList<>();
		
		try {
			MappingIterator<T> objectList  = csvMapper.readerWithSchemaFor(clazz).with(csvSchema).readValues(data);
			
			resulttList = objectList.readAll();
		} catch (IOException ex) {
			if (ex.getMessage().toLowerCase().contains("too many entries")) {
				throw new BadInputException(ResponseMessage.MSG_ERR_CSV_COLUMN_FORMAT);
			}
		}
		return resulttList;

	}
}
