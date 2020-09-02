package scriptie;

import com.charrey.settings.SettingsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static scriptie.ContractionPerformance.directedLatexTest;

public class PruningPerformance {


    @Test
    public void testSerialZeroDomainSmall() throws InterruptedException { //cr
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withSerialPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 1.5, 4.0);
    }

    @Test
    public void testSerialZeroDomainBig() throws InterruptedException {
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withSerialPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withSerialPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 5.0, 4.0);
    }


    @Test
    public void testCachedZeroDomainSmall() throws InterruptedException { //cr
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withCachedPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 1.5, 4.0);
    }

    @Test
    public void testCachedZeroDomainBig() throws InterruptedException {
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withCachedPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 5.0, 4.0);
    }

    @Test
    public void testCachedAlldiffSmall() throws InterruptedException { //cr
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withCachedPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 1.5, 4.0);
    }

    @Test
    public void testCachedAlldiffBig() throws InterruptedException {
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withCachedPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withCachedPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 5.0, 4.0);
    }


    @Test
    public void testParallelZeroDomainSmall() throws InterruptedException { //cr
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withParallelPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 1.5, 4.0);
    }

    @Test
    public void testParallelZeroDomainBig() throws InterruptedException {
        List<Configuration> configurations = new LinkedList<>();
        configurations.add(new Configuration("*",        "blue"  , "K-Path"   , new SettingsBuilder().withKPathRouting().withParallelPruning().withZeroDomainPruning().get(),  new SettingsBuilder().withKPathRouting().get()));
        configurations.add(new Configuration("x",        "red"   , "DFS"      , new SettingsBuilder().withInplaceDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceDFSRouting().get()));
        configurations.add(new Configuration("+",        "green" , "CP"       , new SettingsBuilder().withControlPointRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withControlPointRouting().get()));
        configurations.add(new Configuration("o",        "purple", "GDFS O IP", new SettingsBuilder().withInplaceOldGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        configurations.add(new Configuration("asterisk", "yellow", "GDFS A IP", new SettingsBuilder().withInplaceNewGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(), new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        configurations.add(new Configuration("star",     "gray"  , "GDFS C"   , new SettingsBuilder().withCachedGreedyDFSRouting().withParallelPruning().withZeroDomainPruning().get(),new SettingsBuilder().withCachedGreedyDFSRouting().get()));
        directedLatexTest(configurations, 3.0, 5.0, 4.0);
    }



}
