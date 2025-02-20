/****************************************************************************************
 * @file  Table.java
 *
 * @author   John Miller
 *
 * compile javac --enable-preview --release 21 *.java
 * run     java --enable-preview MovieDB
 */

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;


import static java.lang.System.arraycopy;
import static java.lang.System.out;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/****************************************************************************************
 * The Table class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
       implements Serializable
{
    /** Relative path for storage directory
     */
    private static final String DIR = Paths.get(System.getProperty("user.dir")) + File.separator + "store" + File.separator;

    /** Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /** Counter for naming temporary tables.
     */
    private static int count = 0;

    /** Table name.
     */
    private final String name;

    /** Array of attribute names.
     */
    private final String [] attribute;

    /** Array of attribute domains: a domain may be
     *  integer types: Long, Integer, Short, Byte
     *  real types: Double, Float
     *  string types: Character, String
     */
    private final Class [] domain;

    /** Collection of tuples (data storage).
     */
    private final List <Comparable []> tuples;

    /** Primary key (the attributes forming).
     */
    private final String [] key;

    /** Index into tuples (maps key to tuple).
     */
    private final Map <KeyType, Comparable []> index;
//    private final String name;

    /** The supported map types.
     */
    private enum MapType { NO_MAP, TREE_MAP, HASH_MAP, LINHASH_MAP, BPTREE_MAP }

    /** The map type to be used for indices.  Change as needed.
     */
    private static final MapType mType = MapType.HASH_MAP;

    /************************************************************************************
     * Make a map (index) given the MapType.
     */
    private static Map <KeyType, Comparable []> makeMap ()
    {
        return switch (mType) {
        case NO_MAP      -> null;
        case TREE_MAP    -> new TreeMap <> ();
        case HASH_MAP    -> new HashMap <> ();
        case LINHASH_MAP -> new LinHashMap <> (KeyType.class, Comparable [].class);
        case BPTREE_MAP  -> new BpTreeMap <> (KeyType.class, Comparable [].class);
        default          -> null;
        }; // switch
    } // makeMap

    /************************************************************************************
     * Concatenate two arrays of type T to form a new wider array.
     *
     * @see "https://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java"
     *
     * @param arr1  the first array
     * @param arr2  the second array
     * @return  a wider array containing all the values from arr1 and arr2
     */
    public static <T> T [] concat (T [] arr1, T [] arr2)
    {
        T [] result = Arrays.copyOf (arr1, arr1.length + arr2.length);
        arraycopy (arr2, 0, result, arr1.length, arr2.length);
        return result;
    } // concat

    //-----------------------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = new ArrayList <> ();
//        tuples    = new FileList(_name, 300);
        index     = makeMap ();
//        out.println("Index:"+ index);
    } // constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key,
                  List <Comparable []> _tuples)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = _tuples;
        index     = makeMap ();
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name       the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String attributes, String domains, String _key)
    {
        this (_name, attributes.split (" "), findClass (domains.split (" ")), _key.split(" "));

//        out.println (STR."DDL> create table \{name} (\{attributes})");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     * Projects specified attributes from the current table and creates a new table
     * with the projected attributes. The method extracts the attributes from the existing
     * tuples and adjusts domain, key, and other properties accordingly.
     *
     * #usage movie.project ("title year studioNo")
     *
     * @param attributes  the attributes to project onto
     * @return  a table of projected tuples
     */
    public Table project (String attributes)
    {
        out.println ("RA> " + name + ".project (" + attributes + ")");
        var attrs     = attributes.split (" ");

        var colDomain = extractDom (match (attrs), domain);
        var newKey    = (Arrays.asList (attrs).containsAll (Arrays.asList (key))) ? key : attrs;


        HashSet<String> hs=new HashSet<>();
        for(int i=0;i<attrs.length;i++){
            hs.add(attrs[i]);
        }
        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D
        for(int i=0;i<tuples.size();i++){
            var curr=tuples.get(i);
            var ans=new Comparable[attrs.length];
            var count=0;
            for(int j=0;j<curr.length;j++) {
//                System.out.print(attribute[j]+" ");
                if (hs.contains(attribute[j])) {
//                    System.out.print("/"+attribute[j]+" "+ans[count]+" "+curr[i]+"\\");
                    ans[count] = curr[j];
                    count++;
                }
            }
            rows.add(ans);
        }

        return new Table (name + count++, attrs, colDomain, newKey, rows);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     * Selects tuples from the current table based on the provided predicate
     * and creates a new table containing only the selected tuples.
     *
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @param predicate  the check condition for tuples
     * @return  a table with tuples satisfying the predicate
     */
    public Table select (Predicate <Comparable []> predicate)
    {
//        out.println (STR."RA> \{name}.select (\{predicate})");
        return new Table (name + count++, attribute, domain, key,
                   tuples.stream ().filter (t -> predicate.test (t))
                                   .collect (Collectors.toList ()));
    } // select
    /************************************************************************************
     *
     */
    private Comparable convertToComparable(String input, Class targetClass) {
        if (input == null || targetClass == null) {
            return null;
        }

        switch (targetClass.getSimpleName()) {
            case "Long":
                return Long.parseLong(input);
            case "Integer":
                return Integer.parseInt(input);
            case "Short":
                return Short.parseShort(input);
            case "Byte":
                return Byte.parseByte(input);
            case "Double":
                return Double.parseDouble(input);
            case "Float":
                return Float.parseFloat(input);
            case "Character":
                if (input.length() == 1) {
                    return input.charAt(0);
                }
                break;
            case "String":
                return input;
        }

        System.out.println("Conversion to " + targetClass.getSimpleName() + " not supported.");
        return null;
    }
    /************************************************************************************
     * Select the tuples satisfying the given simple condition on attributes/constants
     * compared using an <op> ==, !=, <, <=, >, >=.
     * Selects tuples from the table based on a simple condition and creates a new table with the selected tuples.
     * #usage movie.select ("year == 1977")
     *
     * @param condition  the check condition as a string for tuples
     * @return  a table with tuples satisfying the condition
     */
    public Table select (String condition)
    {
//        out.println (STR."RA> \{name}.select (\{condition})");

        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D
//        System.out.println("============================================1");
        Pattern regex = Pattern.compile("\\s*(==|!=|<=|<|>=|>)\\s*");
        Matcher match = regex.matcher(condition);
        String[] conditions=new String[0];
        String operator="";

        if (match.find()){
            operator=match.group(1);
            conditions=condition.split(operator);
            for (int i = 0; i < conditions.length; i++) {
                conditions[i] = conditions[i].trim();
            }
//            for (String part : conditions) {
//                System.out.println(part);
//            }
        }
        else{
            System.out.println("no operator found");
            return new Table (name + count++, attribute, domain, key, rows);
        }


        int indexofattribute=-1;
        for(int i=0;i<attribute.length;i++){
            if(conditions[0].equals(attribute[i])){
                indexofattribute=i;
                break;
            }
        }
        if(indexofattribute==-1 || operator.equals("")){
            return new Table (name + count++, attribute, domain, key, rows);
        }
        for(int i=0;i<tuples.size();i++){
            var currrow=tuples.get(i);
            boolean satisfied=false;
            switch (operator){
                case "==":
                      satisfied=currrow[indexofattribute].equals(convertToComparable(conditions[1],domain[indexofattribute]));
                    break;
                case "!=":
                    satisfied=!currrow[indexofattribute].equals(convertToComparable(conditions[1],domain[indexofattribute]));
                    break;
                case "<":
                    satisfied=currrow[indexofattribute].compareTo(convertToComparable(conditions[1],domain[indexofattribute]))<0;
                    break;
                case "<=":
                    satisfied=currrow[indexofattribute].compareTo(convertToComparable(conditions[1],domain[indexofattribute]))<=0;
                    break;
                case ">":
                    satisfied=currrow[indexofattribute].compareTo(convertToComparable(conditions[1],domain[indexofattribute]))>0;
                    break;
                case ">=":
                    satisfied=currrow[indexofattribute].compareTo(convertToComparable(conditions[1],domain[indexofattribute]))>=0;
                    break;
            }
            if(satisfied){
                rows.add(currrow);
            }
        }
//        System.out.println("============================================2");
        return new Table (name + count++, attribute, domain, key, rows);
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.  INDEXED SELECT algorithm.
     * The select method takes a KeyType parameter called keyVal and returns a table with the tuple satisfying the key predicate
     *
     * @param keyVal  the given key value
     * @return  a table with the tuple satisfying the key predicate
     */
    public Table select (KeyType keyVal)
    {
//        out.println (STR."RA> \{name}.select (\{keyVal})");

        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D  - Project 2
        if((mType == MapType.NO_MAP ) ) {
//            out.println("TO SEARCH WITH A KEYVAL YOU NEED TO USE A MAP");
        }
        else{
//            out.println();
            var v = index.get(keyVal);
            rows.add(v);

        }
        return new Table (name + count++, attribute, domain, key, rows);
    } // select

    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible.
     * The union operation includes all unique tuples from both tables.
     * Performs the union operation on two tables, creating a new table with unique tuples.
     * #usage movie.union (show)
     *
     * @param table2  the rhs table in the union operation
     * @return  a table representing the union
     */
    public Table union (Table table2)
    {
        out.println (STR."RA> \{name}.union (\{table2.name})");
        if (! compatible (table2)) return null;

        List <Comparable []> rows = new ArrayList <> ();
        if((mType == MapType.NO_MAP || table2.mType == MapType.NO_MAP) ){
            //  T O   B E   I M P L E M E N T E D
            for (int i = 0; i < tuples.size(); i++) {
                rows.add(tuples.get(i).clone());
            }
            for (int i = 0; i < table2.tuples.size(); i++) {
                rows.add(table2.tuples.get(i).clone());
            }
            return new Table (name + count++, attribute, domain, key, rows);
        }
        // Indexing for the union

        // Iterate the keys of index of table 1
        for (var entry : index.entrySet()) {
            out.println(entry.getKey()+" "+entry.getValue());
            rows.add(entry.getValue()); // Insert directly from index
        }
        //Iterate the keys of index of table 2
        for (var entry : table2.index.entrySet()) {
           if(!index.containsKey(entry.getKey())) {
               rows.add(entry.getValue()); // Insert directly from index
           }
        }



        return new Table (name + count++, attribute, domain, key, rows);
    } // union

    /************************************************************************************
     * Take the difference of this table and table2.  Check that the two tables are
     * compatible.
     * Performs the set difference operation on two tables, creating a new table with tuples
     * that exist in the current table but not in the specified table2.
     * The operation includes all unique tuples from the current table that are not present in table2.
     *
     * #usage movie.minus (show)
     *
     * @param table2  The rhs table in the minus operation
     * @return  a table representing the difference
     */
    public Table minus (Table table2)
    {
        out.println (STR."RA> \{name}.minus (\{table2.name})");
        if (! compatible (table2)) return null;

        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D
        // If either table does not use indexing, fall back to regular iteration
        if (mType == MapType.NO_MAP || table2.mType == MapType.NO_MAP) {
            for (int i = 0; i < tuples.size(); i++) {
                var currMain = tuples.get(i);
                boolean isPresentInTable2 = false;

                for (int j = 0; j < table2.tuples.size(); j++) {
                    var currSecondary = table2.tuples.get(j);
                    boolean currentSecondRowIsSameAsFirst = true;
                    for (int k = 0; k < currMain.length; k++) {
                        if (!currMain[k].equals(currSecondary[k])) {
                            currentSecondRowIsSameAsFirst = false;
                            break;
                        }
                    }
                    if (currentSecondRowIsSameAsFirst) {
                        isPresentInTable2 = true;
                        break;
                    }
                }

                if (!isPresentInTable2) {
                    rows.add(currMain.clone());
                }
            }
        } else {
            // If both tables use indexing, iterate through the keys of the index
            for (var entry : index.entrySet()) {
                Comparable[] currMain = entry.getValue();
                Comparable[] currSecondary = table2.index.get(entry.getKey());

                // Check if the row exists in table2's index
                if (currSecondary == null || !Arrays.equals(currMain, currSecondary)) {
                    rows.add(currMain); // Add to result if not found in table2
                }
            }
        }

        return new Table (name + count++, attribute, domain, key, rows);
    } // minus

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by appending "2" to the end of any duplicate attribute name.  Implement using
     * a NESTED LOOP JOIN ALGORITHM.
     *
     * Performs a join operation on two tables based on specified attributes,
     * creating a new table with combined tuples where the values of the specified attributes match.
     *
     * #usage movie.join ("studioName", "name", studio)
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2       the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (String attributes1, String attributes2, Table table2)
    {
//        out.println (STR."RA> \{name}.join (\{attributes1}, \{attributes2}, \{table2.name})");

        var t_attrs = attributes1.split (" ");
        var u_attrs = attributes2.split (" ");
        var rows    = new ArrayList <Comparable []> ();


        //  T O   B E   I M P L E M E N T E D

        int col_of_t_attrs=0;
        int col_of_u_attrs=0;
        for(int i=0;i<attribute.length;i++){
            if(attribute[i].equals(t_attrs[0])){
                col_of_t_attrs=i;
                break;
            }
        }
        for(int i=0;i<table2.attribute.length;i++){
            if(table2.attribute[i].equals(u_attrs[0])){
                col_of_u_attrs=i;
                break;
            }
        }
        for(int i=0;i<tuples.size();i++){
            var curradd=new Comparable[attribute.length+table2.attribute.length];
            var currfirstrow=tuples.get(i);
            int count=0;
            for(int j=0;j<currfirstrow.length;j++){
                curradd[count++]=currfirstrow[j];
            }
            for(int j=0;j<table2.tuples.size();j++){
                var currsecondrow=table2.tuples.get(j);
                if(currfirstrow[col_of_t_attrs].equals(currsecondrow[col_of_u_attrs])){
                    for(int k=0;k<currsecondrow.length;k++){
                        if(k==col_of_u_attrs)
                        {
                            curradd[count++]=currsecondrow[k]+"2";
                        }
                            else{
                            curradd[count++]=currsecondrow[k];
                        }

                    }

                    break;
                }
            }
            rows.add(curradd);
        }

        return new Table (name + count++, concat (attribute, table2.attribute),
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing a "theta-join".  Tuples from both tables
     * are compared attribute1 <op> attribute2.  Disambiguate attribute names by appending "2"
     * to the end of any duplicate attribute name.  Implement using a Nested Loop Join algorithm.
     *
     * Performs a "theta-join" operation on two tables using a Nested Loop Join algorithm.
     * Tuples from both tables are compared based on the specified condition, where attribute1 <op> attribute2.
     *
     * #usage movie.join ("studioName == name", studio)
     *
     * @param condition  the theta join condition
     * @param table2     the rhs table in the join operation
     * @return  a table with tuples satisfying the condition
     */
    public Table join (String condition, Table table2)
    {
//        out.println (STR."RA> \{name}.join (\{condition}, \{table2.name})");

        var rows = new ArrayList <Comparable []> ();

        //  T O   B E   I M P L E M E N T E D
        var conditions=condition.split(" ");
        // for(int i=0;i<conditions.length;i++){
        //     System.out.println(conditions[i]);
        // }
        int col_of_left=-1;
        int col_of_right=-1;
        for(int i=0;i<attribute.length;i++){
            if(attribute[i].equals(conditions[0])){
                col_of_left=i;
                break;
            }
        }
        for(int i=0;i<table2.attribute.length;i++){
            if(table2.attribute[i].equals(conditions[2])){
                col_of_right=i;
                break;
            }
        }
        for(int i=0;i<tuples.size();i++){
            var currleft=tuples.get(i);
            for(int j=0;j<table2.tuples.size();j++){
                var currright=table2.tuples.get(j);
                boolean satisfied=false;
                switch (conditions[1]){
                    case "==":
                        satisfied=currleft[col_of_left].equals(currright[col_of_right]);
                        break;
                    case "!=":
                        satisfied=!currleft[col_of_left].equals(currright[col_of_right]);
                        break;
                    case "<":
                        satisfied=currleft[col_of_left].compareTo(currright[col_of_right])<0;
                        break;
                    case "<=":
                        satisfied=currleft[col_of_left].compareTo(currright[col_of_right])<=0;
                        break;
                    case ">":
                        satisfied=currleft[col_of_left].compareTo(currright[col_of_right])>0;
                        break;
                    case ">=":
                        satisfied=currleft[col_of_left].compareTo(currright[col_of_right])>=0;
                        break;
                    default:
                        System.out.println("wrong operator");
                }
                if(satisfied){
                    var curradd=new Comparable[attribute.length+table2.attribute.length];
                    int count=0;
                    for(int k=0;k<currleft.length;k++){
                        curradd[count++]=currleft[k];
                    }
                    for(int k=0;k<currright.length;k++){
                        if(k==col_of_right){
                            curradd[count++]=currright[k]+"2";
                        }
                        else{
                            curradd[count++]=currright[k];
                        }

                    }
                    rows.add(curradd);
                }
            }
        }

        return new Table (name + count++, concat (attribute, table2.attribute),
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above equi-join,
     * but implemented using an INDEXED JOIN algorithm.
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2       the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table i_join (String attributes1, String attributes2, Table table2)
    {
        //  T O   B E   I M P L E M E N T E D  - Project 2
//        out.println (STR."RA> \{name}.join (\{attributes1}, \{attributes2}, \{table2.name})");
        var rows    = new ArrayList <Comparable []> ();
        if((mType == MapType.NO_MAP ) ) {
//            out.println("TO DO A IJOIN PLEASE USE A MAP TO USE INDEXING");
            return new Table (name + count++, concat (attribute, table2.attribute),
                    concat (domain, table2.domain), key, rows);
        }
        var t_attrs = attributes1.split (" ");
        var u_attrs = attributes2.split (" ");

//        System.out.println(index);
//        System.out.println(key);


        int[] primarykeyattributes=new int[key.length];

        int count=0;
        for(int i=0;i<attribute.length;i++){
            for(int j=0;j<t_attrs.length;j++) {
                if (attribute[i].equals(t_attrs[j])) {
                    primarykeyattributes[count++]= i;
                    break;
                }
            }

        }

        for(int i=0;i<tuples.size();i++){
            var combinedkeyval = new Comparable [primarykeyattributes.length];
            for (int j=0;j<primarykeyattributes.length;j++) {
                combinedkeyval[j]=tuples.get(i)[primarykeyattributes[j]];
            }

//            out.println(table2.index.get(new KeyType(combinedkeyval)));
            var lookupvalue=table2.index.get(new KeyType(combinedkeyval));
            if(lookupvalue!=null){
                rows.add(concat(tuples.get(i),lookupvalue));
            }
//            out.println(table2.index);
//            out.println("=++++++++++++++++++++++++++++++++++++++++++++++");
        }
        return new Table (name + count++, concat (attribute, table2.attribute),
                concat (domain, table2.domain), key, rows);


    } // i_join


    /************************************************************************************
     * Join this table and table2 by performing an NATURAL JOIN.  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     * Finds common attributes between this table and the specified table.
     * Finds common attributes between two tables, facilitating the NATURAL JOIN operation.
     *
     * #usage movieStar.join (starsIn)
     *
     * @param table2  the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    private List<String> find_common_attributes(Table table2) {
        List<String> commonAttributesAmongTable = new ArrayList<>();

        for (String attr1 : attribute) {
            for (String attr2 : table2.attribute) {
                if (attr1.equals(attr2)) {
                    commonAttributesAmongTable.add(attr1);
                    break;
                }
            }
        }

        return commonAttributesAmongTable;
    }
    /**
     * Checks if two tuples from different tables match based on the values of common attributes.
     *
     * @param tuple1 The first tuple from this table.
     * @param tuple2 The second tuple from the specified table2.
     * @param commonAttributes A list of common attributes shared between the two tables.
     * @param table2 The second table for which the second tuple belongs.
     * @return true if the tuples match on common attributes, false otherwise.
     */
    private boolean tuplesMatch(Comparable[] tuple1, Comparable[] tuple2, List<String> commonAttributes,Table table2) {
        for (String attr : commonAttributes) {
            int index1 = col(attr);
            int index2 = table2.col(attr);
            if (!tuple1[index1].equals(tuple2[index2])) {
                return false;
            }
        }
        return true;
    }
    /**
     * Deletes duplicate attributes from the combined list of attributes, keeping only one occurrence
     * of common attributes and appending unique attributes from the specified list.
     *
     * @param commonAttributes A list of common attributes shared between the two tables.
     * @param attributes The list of attributes to be appended to the combined list.
     * @return An array of attributes without duplicates, combining the common attributes
     *         from this table and the unique attributes from the specified list.
     */
    private String[] delete_duplicate_attributes(List<String> commonAttributes, String[] attributes) {
        List<String> combinedAttributes = new ArrayList<>();

        for (String attr : attribute) {

            combinedAttributes.add(attr);
        }

        for (String attr : attributes) {
            if (!commonAttributes.contains(attr)) {
                combinedAttributes.add(attr);
            }
        }

        return combinedAttributes.toArray(new String[0]);
    }
    /**
     * Performs a NATURAL JOIN operation on this table and the specified table2.
     * Tuples from both tables are compared requiring common attributes to be equal,
     * and the duplicate column is eliminated. The result is a new table with combined tuples.
     *
     * #usage movie.join (starsIn)
     *
     * @param table2 The second table (rhs table) in the join operation.
     * @return A new table with tuples satisfying the equality predicate.
     *         The result includes only one occurrence of common attributes and unique attributes from both tables.
     */
    public Table join (Table table2)
    {
        out.println (STR."RA> \{name}.join (\{table2.name})");

        var rows = new ArrayList <Comparable []> ();

        //  T O   B E   I M P L E M E N T E D

        // Step 1 : Find the common attributes between current table and table2
        List<String> commonAttributesAmongTables = find_common_attributes(table2);


        // Step 2: If there are no common attributes, perform cartesian product
        if (commonAttributesAmongTables.isEmpty()) {
            for (int i = 0; i < tuples.size(); i++) {
                for (int j = 0; j < table2.tuples.size(); j++) {
                    Comparable[] tuple1 = tuples.get(i);
                    Comparable[] tuple2 = table2.tuples.get(j);
                    Comparable[] combinedTuple = new Comparable[tuple1.length + tuple2.length];
                    System.arraycopy(tuple1, 0, combinedTuple, 0, tuple1.length);
                    System.arraycopy(tuple2, 0, combinedTuple, tuple1.length, tuple2.length);
                    rows.add(combinedTuple);
                }
            }
        } else {
            // Step 3: Perform natural join on common attributes

        for (int i = 0; i < tuples.size(); i++) {
            var tuple1 = tuples.get(i);

            for (int j = 0; j < table2.tuples.size(); j++) {
                var tuple2 = table2.tuples.get(j);

                // Check if tuples match on common attributes
                List<Comparable> combinedList = new ArrayList<>();

                // Add elements from the first tuple
                if(tuplesMatch(tuple1,tuple2,commonAttributesAmongTables,table2)){
                    for (int t = 0; t < tuple1.length; t++) {

                        if (commonAttributesAmongTables.contains(attribute[t])) {

                            combinedList.add(tuple1[t]);
                        }
                    }
                    // FIX - eliminate duplicates
                    for ( int t= 0; t < tuple2.length; t++) {
                        if (!commonAttributesAmongTables.contains(table2.attribute[t])) {
                            combinedList.add(tuple2[t]);
                        }
                    }

                    rows.add(combinedList.toArray(new Comparable[0]));
                }

            }
        }
        }

        // Step 4: Eliminate duplicate columns names to show in the table column
        String[] combinedAttributes = delete_duplicate_attributes(commonAttributesAmongTables, table2.attribute);

        return new Table (name + count++, combinedAttributes,
                                          concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Return the column position for the given attribute name or -1 if not found.
     *
     * @param attr  the given attribute name
     * @return  a column position
     */
    public int col (String attr)
    {
        for (var i = 0; i < attribute.length; i++) {
           if (attr.equals (attribute [i])) return i;
        } // for

        return -1;       // -1 => not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     *
     * #usage movie.insert ("Star_Wars", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup  the array of attribute values forming the tuple
     * @return  whether insertion was successful
     */
    public boolean insert (Comparable [] tup)
    {
//        out.println (STR."DML> insert into \{name} values (\{Arrays.toString (tup)})");

        if (typeCheck (tup)) {
            tuples.add (tup);
            var keyVal = new Comparable [key.length];
            var cols   = match (key);
            for (var j = 0; j < keyVal.length; j++) keyVal [j] = tup [cols [j]];
            if (mType != MapType.NO_MAP) index.put (new KeyType (keyVal), tup);

            return true;
        } else {
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return  the table's name
     */
    public String getName ()
    {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print ()
    {
        out.println (STR."\n Table \{name}");
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
        out.print ("| ");
        for (var a : attribute) out.printf ("%15s", a);
        out.println (" |");
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
        for (var tup : tuples) {
            out.print ("| ");
            if(tup!=null){
                for (var attr : tup) out.printf ("%15s", attr);
            }

            out.println (" |");
        } // for
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex ()
    {
        out.println (STR."\n Index for \{name}");
        out.println ("-------------------");
        if (mType != MapType.NO_MAP) {
            for (var e : index.entrySet ()) {

                out.println (STR."\{e.getKey ()} -> \{Arrays.toString (e.getValue ())}");
            } // for
        } // if
        out.println ("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name  the name of the table to load
     */
    public static Table load (String name)
    {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream (DIR + name + EXT));
            tab = (Table) ois.readObject ();
            ois.close ();
        } catch (IOException ex) {
            out.println ("load: IO Exception");
            ex.printStackTrace ();
        } catch (ClassNotFoundException ex) {
            out.println ("load: Class Not Found Exception");
            ex.printStackTrace ();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save ()
    {

        try {

            Path relativePath = Paths.get(System.getProperty("user.dir")).resolve("store");
            if(!Files.exists(relativePath)) Files.createDirectories(relativePath);
            var oos = new ObjectOutputStream (new FileOutputStream (DIR + name + EXT));
            oos.writeObject (this);
            oos.close ();
        } catch (IOException ex) {

            out.println ("save: IO Exception");
            out.println(ex.getMessage());
            ex.printStackTrace ();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2  the rhs table
     * @return  whether the two tables are compatible
     */
    private boolean compatible (Table table2)
    {
        if (domain.length != table2.domain.length) {
            out.println ("compatible ERROR: table have different arity");
            return false;
        } // if
        for (var j = 0; j < domain.length; j++) {
            if (domain [j] != table2.domain [j]) {
                out.println (STR."compatible ERROR: tables disagree on domain \{j}");
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column  the array of column names
     * @return  an array of column index positions
     */
    private int [] match (String [] column)
    {
        int [] colPos = new int [column.length];

        for (var j = 0; j < column.length; j++) {
            var matched = false;
            for (var k = 0; k < attribute.length; k++) {
                if (column [j].equals (attribute [k])) {
                    matched = true;
                    colPos [j] = k;
                } // for
            } // for
            if ( ! matched) out.println (STR."match: domain not found for \{column [j]}");
        } // for

        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t       the tuple to extract from
     * @param column  the array of column names
     * @return  a smaller tuple extracted from tuple t
     */
    private Comparable [] extract (Comparable [] t, String [] column)
    {
        var tup    = new Comparable [column.length];
        var colPos = match (column);
        for (var j = 0; j < column.length; j++) tup [j] = t [colPos [j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in array) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @param t  the tuple as a array of attribute values
     * @return  whether the tuple has the right size and values that comply
     *          with the given domains
     */
    private boolean typeCheck (Comparable [] t)
    {
        //  T O   B E   I M P L E M E N T E D
        // Check if the tuple size matches the domain size
        if (t.length != domain.length) {
            return false;
        }

        // Check each value in the tuple against the corresponding domain
        for (int i = 0; i < t.length; i++) {
            // Check if the value is null
            if (t[i] == null) {
                continue; // Allow null values
            }
            // Check if the value is an instance of the expected domain type
            if (!domain[i].isInstance(t[i])) {
                return false; // Type mismatch
            }
        }
        // All checks passed, the tuple is valid
        return true;    // change once implemented
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return  an array of Java classes
     */
    private static Class [] findClass (String [] className)
    {
        var classArray = new Class [className.length];

        for (var i = 0; i < className.length; i++) {
            try {
                classArray [i] = Class.forName (STR."java.lang.\{className [i]}");
            } catch (ClassNotFoundException ex) {
                out.println (STR."findClass: \{ex}");
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos  the column positions to extract.
     * @param group   where to extract from
     * @return  the extracted domains
     */
    private Class [] extractDom (int [] colPos, Class [] group)
    {
        var obj = new Class [colPos.length];

        for (var j = 0; j < colPos.length; j++) {
            obj [j] = group [colPos [j]];
        } // for

        return obj;
    } // extractDom

} // Table

