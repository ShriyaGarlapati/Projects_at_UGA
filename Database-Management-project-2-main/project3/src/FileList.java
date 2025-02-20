
/*******************************************************************************
 * @file  FileList.java
 *
 * @author   John Miller
 */

import java.io.*;
import static java.lang.System.out;
import java.util.*;

/*******************************************************************************
 * This class allows data tuples/tuples (e.g., those making up a relational table)
 * to be stored in a random access file.  This implementation requires that each
 * tuple be packed into a fixed length byte array.
 */
public class FileList
       extends AbstractList <Comparable []>
       implements List <Comparable []>, RandomAccess,Serializable
{
    /** File extension for data files.
     */
    private static final String EXT = ".dat";

    /** The random access file that holds the tuples.
     */
    private transient RandomAccessFile file;

    /** The name of table.
     */
    private final String tableName;

    /** The number bytes required to store a "packed tuple"/record.
     */
    private final int recordSize;

    /** Counter for the number of tuples in this list.
     */
    private int nRecords = 0;
    private int length_of_file = 0;
    private int actual_byte = 0;

    /***************************************************************************
     * Construct a FileList.
     * @param _tableName   the name of the table
     * @param _recordSize  the size of tuple in bytes.
     */
     public FileList(String _tableName, int _recordSize) {
        this.tableName = _tableName;
        this.recordSize = _recordSize;

        try {
            this.file = new RandomAccessFile(this.tableName + ".dat", "rw");
            this.file.seek(0L);
        } catch (Exception var4) {
            this.file = null;
            System.out.println("FileList.constructor: unable to open - " + String.valueOf(var4));
        }

    }
 private byte[] pack(Comparable[] tuple) {
        byte[] fixedBytes = new byte[this.recordSize];

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(tuple);
            oos.flush();
            if (bos.size() < this.recordSize) {
                System.out.println(bos.size());
                byte[] garbageBytes = new byte[this.recordSize - bos.size() - 2];
                oos.write(garbageBytes);
                oos.flush();
//                System.out.println(garbageBytes.length);
//                System.out.println(bos.size());
//                System.out.println(bos.toByteArray().length);
            }

            if (bos.size() > this.recordSize) {
                throw new Exception("The byte size of tuple is more than record size please increase the size of record");
            } else {
                return bos.toByteArray();
            }
        } catch (Exception var6) {
            System.out.println(var6);
            return fixedBytes;
        }
    }

    private Comparable[] unpack(byte[] packedBytes) {
        try {
//            System.out.println("Getting the object");
//            System.out.println(packedBytes.length);
            ByteArrayInputStream bis = new ByteArrayInputStream(packedBytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
//            System.out.println(bis.available());
            Object o = ois.readObject();
            return (Comparable[])o;
        } catch (Exception var5) {
//            System.out.println(var5.getMessage());
            var5.printStackTrace();
            return null;
        }
    }

    /***************************************************************************
     * Add a new tuple into the file list by packing it into a record and writing
     * this record to the random access file.  Write the record either at the
     * end-of-file or into a empty slot.
     * @param tuple  the tuple to add
     * @return  whether the addition succeeded
     */
    public boolean add(Comparable[] tuple) {
        byte[] record = this.pack(tuple);
        if (record.length != this.recordSize) {
//            System.out.println(record);
//            System.out.println("Record Size:" + this.recordSize);
//            System.out.println("FileList.add: wrong record size " + record.length);
            return false;
        } else {
            try {
                this.file.seek((long)this.length_of_file);
                this.file.write(record);
                this.length_of_file += record.length;
                ++this.nRecords;
                return true;
            } catch (IOException var4) {
                System.out.println("FileList.add: error writing record - " + String.valueOf(var4));
                return false;
            }
        }
    }

    /***************************************************************************
     * Get the ith tuple by seeking to the correct file position and reading the
     * record.
     * @param i  the index of the tuple to get
     * @return  the ith tuple
     */
   public Comparable[] get(int i) {
        byte[] record = new byte[this.recordSize];

        try {
//            System.out.println(i);
            this.file.seek((long)(i * this.recordSize));
            this.file.read(record);
            return this.unpack(record);
        } catch (IOException var4) {
            System.out.println("FileList.get: error reading record - " + String.valueOf(var4));
            return null;
        }
    }
    /***************************************************************************
     * Return the size of the file list in terms of the number of tuples/records.
     * @return  the number of tuples
     */
    public int size() {
        return this.nRecords;
    } // size

    /***************************************************************************
     * Close the file.
     */
    public void close() {
        try {
            this.file.close();
        } catch (IOException var2) {
            System.out.println("FileList.close: unable to close - " + String.valueOf(var2));
        }

    } // close

} // FileList class

