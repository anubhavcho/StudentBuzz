package edu.studentbuzz.helper;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;
public class FileUploader {
	//Properties
	private long sizelimit;
	private ArrayList < String > allowed_filetype;
	private String upload_dir;
	//Inputs
	private HttpServletRequest request;
	private ArrayList < String > field_names;
	//Outputs
	private ArrayList < String > assigined_name;
	private ArrayList < String > original_name;
	private ArrayList < Long > size;
	private ArrayList < String > file_type;
	
	public ArrayList < String > getAllowed_filetype() {
		return allowed_filetype;
	}
	public void setAllowed_filetype(ArrayList < String > allowed_filetype) {
		this.allowed_filetype = allowed_filetype;
	}
	public ArrayList < String > getField_names() {
		return field_names;
	}
	public void setField_names(ArrayList < String > field_names) {
		this.field_names = field_names;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public long getSizelimit() {
		return sizelimit;
	}
	public void setSizelimit(long sizelimit) {
		this.sizelimit = sizelimit;
	}
	public String getUpload_dir() {
		return upload_dir;
	}
	public void setUpload_dir(String upload_dir) {
		this.upload_dir = upload_dir;
	}
	public ArrayList < String > getAssigined_name() {
		return assigined_name;
	}
	public ArrayList < String > getFile_type() {
		return file_type;
	}
	public ArrayList < String > getOriginal_name() {
		return original_name;
	}
	public ArrayList < Long > getSize() {
		return size;
	}
	public void upload() {
		//Required Initilization
		CopyFile cpy = new CopyFile();
		String fieldname;
		assigined_name = new ArrayList < String > ();
		original_name = new ArrayList < String > ();
		file_type = new ArrayList < String > ();
		size = new ArrayList < Long > ();
		Iterator < String > fieldlist = field_names.iterator();
		while (fieldlist.hasNext()) {
			try {
				fieldname = fieldlist.next();
				if (request.getPart(fieldname).getSize() <= sizelimit) {
					if (match(request.getPart(fieldname).getContentType())) {
						String tname = genNewName(request.getPart(fieldname).getHeader("content-disposition"));
						cpy.copyFile(request.getPart(fieldname).getInputStream(), new File(request.getSession().getServletContext().getRealPath("/").replace('\\', '/') + "/"+upload_dir + "/" + tname));

						assigined_name.add(tname);
						original_name.add(getFname(request.getPart(fieldname).getHeader("content-disposition")));
						file_type.add(request.getPart(fieldname).getContentType());
						size.add(request.getPart(fieldname).getSize());
					}

				}
			} catch (Exception e) {
				//System.out.println(e.toString());
				e.printStackTrace();
			}

		}
	}

	private boolean match(String type) {
		String typel;
		Iterator < String > typelist = allowed_filetype.listIterator();
		while (typelist.hasNext()) {
			typel = typelist.next();
			if (typel.equals(type)) return true;
		}
		return false;
	}

	private String genNewName(String data) {
		StringBuilder sb = null;
		String ext = getFname(data).substring(getFname(data).lastIndexOf('.'));
		String fname = getFname(data) + Math.random() * 65536;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(fname.getBytes());
			byte[] digest = md.digest();
			sb = new StringBuilder();
			for (byte b: digest) {
				sb.append(Integer.toHexString((int)(b & 0xff)));
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		File file = new File(request.getSession().getServletContext().getRealPath("/").replace('\\', '/') + "/" + upload_dir + "\\" + sb.toString() + ext);

		if (file.exists()) return genNewName(data);
		return sb.toString() + ext;
	}

	static private String getFname(String data) {
		String result;
		result = data.substring(data.indexOf("filename=",0)+10, data.indexOf("\"", data.indexOf("filename=",0)+10));
		return result;
	}


}