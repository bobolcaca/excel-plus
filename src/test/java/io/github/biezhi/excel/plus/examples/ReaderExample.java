package io.github.biezhi.excel.plus.examples;

import io.github.biezhi.excel.plus.BaseTest;
import io.github.biezhi.excel.plus.Reader;
import io.github.biezhi.excel.plus.types.Result;
import io.github.biezhi.excel.plus.exception.ReaderException;
import io.github.biezhi.excel.plus.model.Book;
import io.github.biezhi.excel.plus.model.Financial;
import io.github.biezhi.excel.plus.model.PerformanceTestModel;
import io.github.biezhi.excel.plus.model.Sample;
import io.github.biezhi.excel.plus.types.Valid;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2018-12-11
 */
@Slf4j
public class ReaderExample extends BaseTest {

    @Test
    public void testReadBasic() throws ReaderException {
        List<Financial> financials = Reader.create(Financial.class)
                .from(new File(classPath() + "/FinancialSample.xlsx"))
                .asList();

        log.info("financials size: {}", financials.size());
    }

    @Test
    public void testRead100wRows() throws Exception {
        List<PerformanceTestModel> rows = readyData();
        writeTestExcel(rows);
        log.info("write done !!!");

        long start = System.currentTimeMillis();

        List<PerformanceTestModel> list = Reader.create(PerformanceTestModel.class)
                .from(new File(testFileName))
                .asList();

        long end = System.currentTimeMillis();
        log.info("Read " + list.size() + " rows, time consuming: " + (end - start) + "ms");
        // If you want to open the file view, please comment this line
        Files.delete(Paths.get(testFileName));
    }

    @Test
    public void testReadBySheetIndex() throws ReaderException {
        System.out.println(classPath());
        List<Sample> samples = Reader.create(Sample.class)
                .from(new File(classPath() + "/SampleData.xlsx"))
                .sheet(1)
                .start(1)
                .asList();

        assertEquals(43, samples.size());
        assertEquals(new BigDecimal("189.05"), samples.get(0).getAmount());
        assertEquals(new BigDecimal("139.72"), samples.get(samples.size() - 1).getAmount());
    }

    @Test
    public void testReadBySheetName() throws ReaderException {
        List<Sample> samples = Reader.create(Sample.class)
                .from(new File(classPath() + "/SampleData.xlsx"))
                .sheet("SalesOrders")
                .start(1)
                .asList();

        assertEquals(43, samples.size());
        assertEquals(new BigDecimal("189.05"), samples.get(0).getAmount());
        assertEquals(new BigDecimal("139.72"), samples.get(samples.size() - 1).getAmount());
    }

    @Test
    public void testReadAndFilter() throws ReaderException {
        List<Sample> samples = Reader.create(Sample.class)
                .from(new File(classPath() + "/SampleData.xlsx"))
                .sheet("SalesOrders")
                .start(1)
                .asStream()
                .filter(sample -> sample.getAmount().intValue() > 1000)
                .collect(toList());

        assertEquals(6, samples.size());
        assertEquals(new BigDecimal("1619.19"), samples.get(0).getAmount());
        assertEquals(new BigDecimal("1879.06"), samples.get(samples.size() - 1).getAmount());
    }

    @Test
    public void testReadAndValid() throws ReaderException {
        Result<Sample> result = Reader.create(Sample.class)
                .from(new File(classPath() + "/SampleData.xlsx"))
                .sheet("SalesOrders")
                .start(1)
                .asResult()
                .valid((rowNum, item) -> {
                    if (item.getAmount().intValue() > 1000) {
                        return Valid.error(String.format("第 %d 行金额超过 1000", rowNum));
                    }
                    return Valid.ok();
                });

        log.info("rows size: {}", result.count());
        log.info("success rows size: {}", result.successCount());
        log.info("errorMessages: {}", result.errorMessages());

        assertEquals(6, result.errorCount());
        assertEquals(37, result.successCount());

        assertEquals(new BigDecimal("189.05"), result.successRows().get(0).getAmount());
        assertEquals(new BigDecimal("1619.19"), result.errorRows().get(0).getAmount());
    }

    @Test
    public void testReadCSV() {
        List<Book> books = Reader.create(Book.class)
                .from(new File(classPath() + "/book.csv"))
                .start(0)
                .asList();

        log.info("{}", books);
        assertEquals(5, books.size());
    }

}
