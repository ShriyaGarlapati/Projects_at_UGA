 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import java.util.ArrayList;

import static java.lang.System.out;

/*****************************************************************************************
 * This class tests the TupleGenerator on the Student Registration Database defined in the
 * Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).  The primary keys
 * (see figure 3.6) and foreign keys (see example 3.2.2) are as given in the textbook.
 */
public class TestTupleGenerator
{
    /*************************************************************************************
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        ArrayList<String> selecttimes1=new ArrayList<>();
        ArrayList<String> selecttimes2=new ArrayList<>();
        ArrayList<String> selectnomaptimes1=new ArrayList<>();
        ArrayList<String> selectnomaptimes2=new ArrayList<>();
        ArrayList<String> joinstimes1=new ArrayList<>();
        ArrayList<String> joinstimes2=new ArrayList<>();

        ArrayList<String> nomapjoinstimes1=new ArrayList<>();
        ArrayList<String> nomapjoinstimes2=new ArrayList<>();
//        out.println ();
        ArrayList<Integer> sizearr=new ArrayList<>();
        for(int RUNS=1;RUNS<=10;RUNS++){
            var test = new TupleGeneratorImpl ();
            test.addRelSchema ("Student",
                    "id name address status",
                    "Integer String String String",
                    "id",
                    null);

            test.addRelSchema ("Professor",
                    "id name deptId",
                    "Integer String String",
                    "id",
                    null);

            test.addRelSchema ("Course",
                    "crsCode deptId crsName descr",
                    "String String String String",
                    "crsCode",
                    null);

            test.addRelSchema ("Teaching",
                    "crsCode semester profId",
                    "String String Integer",
                    "crcCode",
                    new String [][] {{ "profId", "Professor", "id" },
                            { "crsCode", "Course", "crsCode" }});

            test.addRelSchema("Transcript",
                    "studId crsCode semester grade",
                    "Integer String String String",
                    "studId",//crsCode semester removed them since to join indexes must be same
                    new String [][] {{ "studId", "Student", "id"},
                            { "crsCode", "Course", "crsCode" },
                            { "crsCode semester", "Teaching", "crsCode semester" }});
            var tables = new String [] { "Student", "Professor", "Course", "Teaching", "Transcript"};

            out.println("RUN==============================================================="+RUNS);
            var size=10000*RUNS; //for join
//            var size=100000+10000*RUNS;  // for select
            sizearr.add(size);
            out.println("size of table for this run is======================================"+size);
            var tups   = new int [] { size,size,size,size,size };//inserting 10000 random values
            var resultTest = test.generate (tups);

            var student = new Table ("Student",
                    "id name address status",
                    "Integer String String String",
                    "id");
            var Professor = new Table ("Professor",
                    "id name deptId",
                    "Integer String String",
                    "id");
            var Course= new Table("Course",
                    "crsCode deptId crsName descr",
                    "String String String String",
                    "crsCode");
            var Teaching=new Table("Teaching",
                    "crsCode semester profId",
                    "String String Integer",
                    "crsCode") ;
            var Transcript=new Table("Transcript",
                    "studId crsCode semester grade",
                    "Integer String String String",
                    "studId");//crsCode semester removed them since to join indexes must be same


            for (var i = 0; i < resultTest.length; i++) {
                for (var j = 0; j < resultTest [i].length; j++) {
                    var tempfilm=new Comparable[resultTest [i][j].length];
                    int count=0;
                    for (var k = 0; k < resultTest [i][j].length; k++) {
//                    out.print (resultTest [i][j][k] + ","); //printing the generated random values
                        tempfilm[count++]=resultTest [i][j][k]; //adding the generated random values
                    } // for
                    if(i==0){
                        student.insert(tempfilm);
                    }
                    if(i==1){
                        Professor.insert(tempfilm);
                    }
                    if(i==2){
                        Course.insert(tempfilm);
                    }
                    if(i==3){
                        Teaching.insert(tempfilm);
                    }
                    if(i==4){
                        Transcript.insert(tempfilm);
                    }
                } // for
//                out.println ();
            } // for


            var quantity_of_select_map=1000000; //run select 1000000 times and divide by 1000000 to get more accurate time
            int num_of_runs_map=5; //average for 5 runs of map select

            var quantity_of_select_nomap=1000; //run select 1000 times and divide by 1000 to get more accurate time
            int num_of_runs_nomap=5;//average for 5 runs of nomap select

            var quantity_of_joins=1000; //run mapped join 1000 times and divide by 1000 to get more accurate time
            int num_of_joinruns=5;//average for 5 runs of mapped join

            var quantity_of_nomap_joins=1; //run no map join 1 times and divide by 1 to get more accurate time
            int num_of_nomap_joinruns=5;//average for 5 runs  nomap join



            ////----------------select map testing by vishal change map to TREE_MAP, HASH_MAP, LINHASH_MAP, BPTREE_MAP and run different codes -------------------//
            out.println("start of running mapped select+++++++++++++++++++++++++");
            runselect( student,quantity_of_select_map,num_of_runs_map,selecttimes1,resultTest,0);
            runselect( Professor,quantity_of_select_map,num_of_runs_map,selecttimes2,resultTest,1);
            out.println("end of running mapped select+++++++++++++++++++++++++");
            out.println();
            //----------------select nomap testing by vishal -------------------//
            out.println("start of running no map select+++++++++++++++++++++++++");
            runnomapselect( quantity_of_select_nomap,student,num_of_runs_nomap,selectnomaptimes1,resultTest,0);
            runnomapselect( quantity_of_select_nomap,Professor,num_of_runs_nomap,selectnomaptimes2,resultTest,1);
            out.println("end of running no map select+++++++++++++++++++++++++");
            out.println();
            //----------------i_join testing by vishal -------------------//
            out.println("start of running mapped join+++++++++++++++++++++++++");
            runjoin( quantity_of_joins,Teaching,Course,"crsCode","crsCode",num_of_joinruns,joinstimes1);
            runjoin( quantity_of_joins,student,Transcript,"id","studId",num_of_joinruns,joinstimes2);
            out.println("end of running mapped join+++++++++++++++++++++++++");
            out.println();
            //----------------no_map join testing by vishal -------------------//
            out.println("start of running no map join+++++++++++++++++++++++++");
            runnomapjoin( quantity_of_nomap_joins,Teaching,Course,"crsCode == crsCode",num_of_nomap_joinruns,nomapjoinstimes1);
            runnomapjoin( quantity_of_nomap_joins,student,Transcript,"id == studId",num_of_nomap_joinruns,nomapjoinstimes2);
            out.println("end of running no map join+++++++++++++++++++++++++");
            out.println();
        }

        //------------output of  mapped select---------------------//
        out.println();
        out.println("size is                                    "+sizearr);
        out.println("time taken for mapped select is milli seconds "+selecttimes1);
        out.println("time taken for mapped select is milli seconds "+selecttimes2);
        out.println();
        out.println();
        //------------output of no map select---------------------//
        out.println();
        out.println("size is                                    "+sizearr);
        out.println("time taken for no map select is milli seconds "+selectnomaptimes1);
        out.println("time taken for no map select is milli seconds "+selectnomaptimes2);
        out.println();
        out.println();

        //------------output of mapped join---------------------//
        out.println();
        out.println("size is                                    "+sizearr);
        out.println("time taken for mapped join is milli seconds "+joinstimes1);
        out.println("time taken for mapped join is milli seconds "+joinstimes2);
        out.println();
        out.println();
        //------------output of no map join---------------------//
        out.println();
        out.println("size is                                    "+sizearr);
        out.println("time taken for no map join is milli seconds "+nomapjoinstimes1);
        out.println("time taken for no map join is milli seconds "+nomapjoinstimes2);
        out.println();
        out.println();
    } // main

    public static  void runselect(Table anytable,int quantity_of_select,int num_of_runs,ArrayList<String> select,Comparable [][][] resultTest,int tablenum){
        double time=0;
        for(int i=0;i<num_of_runs+1;i++){ //take average of 5 selects
            long nano_startTime = System.nanoTime();
            for(int j=0;j<quantity_of_select;j++){ //run select a lot of times
                var table=anytable.select (new KeyType (resultTest[tablenum][(int)Math.floor(resultTest [0].length*Math.random())][0]));//giving select random keys
            }
            long nano_endTime = System.nanoTime();
            if(i>0){
                time += (nano_endTime - nano_startTime) / quantity_of_select;//ignore the first iteration due to jit as told in pdf
            }
        }

        select.add(String.format("%.9f", (time/num_of_runs)/1000000d));
        out.println("Average Time taken in milli seconds for indexed select: "+String.format("%.9f", (time/num_of_runs)/1000000d));


    }
    public static  void runnomapselect(int quantity_of_select,Table anytable,int num_of_runs,ArrayList<String> nomapselect,Comparable [][][] resultTest,int tablenum){
        double time=0;
        for(int j=0;j<num_of_runs+1;j++){
            long nano_startTime = System.nanoTime();
            for(var i=0;i<quantity_of_select;i++){
                anytable.select (STR."id == \{resultTest[tablenum][(int)Math.floor(resultTest [0].length*Math.random())][0]}"); //giving select random keys
            }
            long nano_endTime = System.nanoTime();

            time+=(nano_endTime - nano_startTime)/quantity_of_select;//ignore the first iteration due to jit as told in pdf

        }
        nomapselect.add(String.format("%.9f", (time/num_of_runs)/1000000d));
        out.println("Average Time taken in milli seconds for nomap select: "+String.format("%.9f", (time/num_of_runs)/1000000d));//average of five iterations
    }


    public static  void runjoin(int quantity_of_joins,Table table1,Table table2,String JoinKey1,String JoinKey2,int num_of_runs,ArrayList<String> joins){
        double time=0;

        for(int j=0;j<num_of_runs+1;j++){
            long nano_startTime = System.nanoTime();
            for(var i=0;i<quantity_of_joins;i++){
                var temprun = table1.i_join (JoinKey1,JoinKey2, table2);
            }

            long nano_endTime = System.nanoTime();
            if(j>0){
                time+=((nano_endTime - nano_startTime)/quantity_of_joins)/1000000d;//ignore the first iteration due to jit as told in pdf
            }
        }
        joins.add(String.format("%.9f", time/num_of_runs));
        out.println("Average Time taken in milli seconds for indexed join: "+String.format("%.9f", time/num_of_runs));//average of five iterations
    }

    public static  void runnomapjoin(int quantity_of_joins,Table table1,Table table2,String joincondition,int num_of_runs,ArrayList<String> joins){
        double time=0;

        for(int j=0;j<num_of_runs+1;j++){
            long nano_startTime = System.nanoTime();
            for(var i=0;i<quantity_of_joins;i++){
                var temprun = table1.join (joincondition, table2);
            }
            long nano_endTime = System.nanoTime();
            if(j>0){
                time+=((nano_endTime - nano_startTime)/quantity_of_joins)/1000000d;//ignore the first iteration due to jit as told in pdf
            }
        }
        joins.add(String.format("%.9f", time/num_of_runs));
        out.println("Average Time taken in milli seconds for No_MAP join: "+String.format("%.9f", time/num_of_runs));//average of five iterations
    }

} // TestTupleGenerator

