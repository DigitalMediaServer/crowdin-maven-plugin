package com.digitalmediaserver.crowdin.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a helper class to sort the files according to .properties files' groups/name
 *
 * @author Nadahar
 */
public class LineList<E> {

	private class GroupStruct {
		String name = "";
		int num = -1;
	}

	private class LineStruct {
		E line = null;
		List<GroupStruct> groups = new ArrayList<GroupStruct>();

		public String toString() {
			String result = "";
			for (GroupStruct group : groups) {
				if (!result.equals("")) {
					result += "." + group.name;
				} else {
					result = group.name;
				}
			}
			return result;
		}
	}

	private class LineComparator implements Comparator<LineStruct> {
		public int compare(LineStruct o1, LineStruct o2) {
			int i = 0;
			int j = 0;
			while (i == 0) {
				if (o1.groups.size() > j && o2.groups.size() > j) {
					i = Integer.valueOf(o1.groups.get(j).num).compareTo(Integer.valueOf(o2.groups.get(j).num));
					if (i == 0) {
						i = o1.groups.get(j).name.compareTo(o2.groups.get(j).name);
					}
				} else if (o1.groups.size() > j) {
					i = 1;
				} else if (o2.groups.size() > j) {
					i = -1;
				} else {
					i = 0;
					break;
				}
				j++;
			}
			return i;
		}
	}

	private Pattern pattern = Pattern.compile("^\\s*((?:[\\w-_]+\\.)*\\w+)");
	private Object listLock = new Object();
	private List<LineStruct> lines = new ArrayList<LineStruct>();

    public void add(E e) {
    	LineStruct line = new LineStruct();

    	String key = (String) e;
		Matcher m = pattern.matcher(key);
		if (m.find()) {
			String[] groups = m.group(1).split("\\.");
			for (String group : groups) {
				GroupStruct groupStruct = new GroupStruct();
				groupStruct.name = group;
				try {
					groupStruct.num = Integer.valueOf(group);
				} catch (NumberFormatException e1) {
					// Nothing to do, default value applies
				}
				line.groups.add(groupStruct);
			}
		}
		line.line = e;

    	synchronized(listLock) {
    		lines.add(line);
    	}
    }

    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            public boolean hasMoreElements() {
            	synchronized (listLock) {
            		return count < lines.size();
            	}
            }

            public E nextElement() {
            	synchronized (listLock) {
	                if (count < lines.size()) {
	                    return lines.get(count++).line;
	                }
            	}
                throw new NoSuchElementException("LineList Enumeration");
            }
        };
    }

    public void sort() {
    	synchronized (listLock) {
    		Collections.sort(lines, new LineComparator());
    	}
    }
}
