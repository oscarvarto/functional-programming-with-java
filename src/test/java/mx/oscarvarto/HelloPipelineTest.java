package mx.oscarvarto;

import fj.F;
import fj.Show;
import fj.data.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class HelloPipelineTest {

    @Test
    void listTest1() {
        // NOTE: Logging "Step N" only to demonstrate the execution order
        F<List<Integer>, List<Integer>> step1 = ns -> {
            log.info("Step 1");
            return ns.map(n -> n + 1);
        };

        F<List<Integer>, List<Integer>> step2 = ns -> {
            log.info("Step 2");
            return ns.map(n -> n + 2);
        };

        F<List<Integer>, List<Integer>> step3 = ns -> {
            log.info("Step 3");
            return ns.map(n -> n + 3);
        };

        F<List<Integer>, List<Integer>> step4 = ns -> {
            log.info("Step 4");
            return ns.map(n -> n + 4);
        };

        F<List<Integer>, List<Integer>> pipeline1 = step1
                .andThen(step2)
                .andThen(step3)
                .andThen(step4);

        var input = List.list(1, 2, 3, 4, 5);
        var output = pipeline1.f(input);
        String prettyOutput = Show.listShow(Show.intShow).showS(output);
        log.info(prettyOutput);
    }
}
