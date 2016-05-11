package quickstep;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Tag;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.Element;

public class Feature extends gherkin.formatter.model.Feature {

    private static final long serialVersionUID = -8910198671320000837L;

    public Element[] elements;

    public Feature() {
        this(new ArrayList<Comment>(), new ArrayList<Tag>(), "DEFAULT KEYWORD", "DEFAULT NAME", "DEFAULT DESCRIPRION",
                new Integer(0), "DEFAULT ID");
    }

    public Feature(List<Comment> comments, List<Tag> tags, String keyword, String name, String description,
            Integer line, String id) {
        super(comments, tags, keyword, name, description, line, id);

    }

}
