package com.ums.crowdin.maven.tool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * This is a helper class to sort the files according to .properties files' group and name
 *
 * @author Nadahar
 */
public class LineList<E> {

	private class LineStruct {
		E line = null;
		String group = "";
		String name = "";
		int num = -1;

		public synchronized String toString() {
			return String.format("%s : %d \"%s\"", group, num, line);
		}
	}

	private class LineComparator implements Comparator<LineStruct> {
		@Override
		public int compare(LineStruct o1, LineStruct o2) {
			int i = o1.group.compareTo(o2.group);
			if (i == 0) {
				i = Integer.valueOf(o1.num).compareTo(Integer.valueOf(o2.num));
				if (i == 0) {
					i = o1.name.compareTo(o2.name);
				}
			}
			return i;
		}
	}

	Pattern pattern = Pattern.compile("^\\s*(\\w+)\\.(\\w+)");
	private List<LineStruct> lines = new ArrayList<LineStruct>();

    public void add(E e) {
    	LineStruct line = new LineStruct();

    	String key = (String) e;
		Matcher m = pattern.matcher(key);
		if (m.find()) {
			if (m.groupCount() > 0) {
				line.group = m.group(1);
			}
			if (m.groupCount() > 1) {
				line.name = m.group(2);
				try {
				line.num = Integer.valueOf(m.group(2));
				} catch (NumberFormatException e1) {
					// Nothing to do, default value applies
				}
			}
		}
		line.line = e;

    	synchronized(this) {
    		lines.add(line);
    	}
    }

    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            public synchronized boolean hasMoreElements() {
                return count < lines.size();
            }

            public synchronized E nextElement() {
                if (count < lines.size()) {
                    return lines.get(count++).line;
                }
                throw new NoSuchElementException("LineList Enumeration");
            }
        };
    }

    public synchronized void sort() {
    	Collections.sort(lines, new LineComparator());
    }
}
