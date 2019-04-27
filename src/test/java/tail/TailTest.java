package tail;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TailTest {

    List<String> strings = new ArrayList<String>();
    {
        strings.add("f");
    }

    @Test
    public void t(){
        strings.add("ee");
    }

    @Test
    public void t2(){
        strings.add("e1");
    }
}