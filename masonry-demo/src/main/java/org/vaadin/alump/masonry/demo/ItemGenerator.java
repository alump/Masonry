package org.vaadin.alump.masonry.demo;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.util.Random;

/**
 * Class used to generate items to layouts
 */
public class ItemGenerator {

    private static Random rand = new Random(0xDEADBEEF);


    private final static String BACON_IPSUMS[] = new String[] {
            "Tail flank ribeye sirloin tri-tip, spare ribs kielbasa bresaola short ribs prosciutto. Landjaeger brisket tongue shankle drumstick beef venison pancetta jerky rump. Short ribs frankfurter strip steak filet mignon pastrami bresaola kielbasa doner sausage venison landjaeger tongue pork loin brisket capicola. Pig venison turducken bresaola, fatback ground round frankfurter pork belly tongue. Biltong corned beef rump porchetta prosciutto tri-tip, pork belly frankfurter spare ribs venison tenderloin pastrami. Tri-tip rump pork loin, frankfurter jerky landjaeger swine doner porchetta pork belly meatloaf filet mignon ham. Tenderloin capicola porchetta fatback rump chuck",
            "Tail tongue short loin sausage pancetta pig pork strip steak pork chop beef ribs flank. Biltong fatback bacon spare ribs. Jerky fatback pork chop swine. Pork loin corned beef tongue pork, brisket hamburger cow bresaola boudin pancetta pig capicola. Short loin pancetta andouille, corned beef ribeye landjaeger pork chop shank tenderloin doner spare ribs ham ham hock sirloin. Ham hock ball tip pork bresaola, meatloaf tongue kielbasa pork loin andouille fatback corned beef tail swine. Spare ribs jerky ribeye, shank fatback doner prosciutto andouille capicola short loin leberkas ham hock frankfurter meatball.",
            "Salami cow turkey meatloaf doner tenderloin andouille chuck kevin bacon ham hock pig pork belly. Flank landjaeger rump fatback pork turducken meatball venison drumstick bacon boudin. Frankfurter flank drumstick sirloin, short loin kevin kielbasa andouille ham hock turducken leberkas bresaola t-bone. Beef ribs salami andouille, meatball pancetta chuck chicken swine shoulder spare ribs frankfurter turducken. Beef fatback shankle jerky venison capicola pig porchetta flank ground round landjaeger. Beef ham turkey ground round tri-tip, ham hock corned beef turducken kielbasa. Ball tip filet mignon turkey jowl t-bone tenderloin tail beef ribs.",
            "Jowl rump meatloaf shank cow chicken turducken chuck capicola. Pig kielbasa shankle filet mignon meatball tail ground round drumstick beef. Pork kielbasa tri-tip beef ribs, drumstick salami ham capicola pork chop pastrami tenderloin short loin landjaeger beef. Pancetta meatball kielbasa capicola hamburger tongue rump prosciutto porchetta sirloin salami t-bone jerky. Beef ribs jowl prosciutto hamburger chuck ham hock pork loin corned beef leberkas ribeye pancetta doner pork belly. Shank leberkas tongue, rump tenderloin andouille doner pork chop chicken pastrami corned beef ribeye meatloaf flank short ribs.",
            "Leberkas filet mignon prosciutto brisket, t-bone shank short ribs ribeye andouille spare ribs. Rump leberkas shankle prosciutto chicken. Pig brisket pork belly, leberkas jowl corned beef beef ribs capicola. Shankle kielbasa pancetta, ham hock tail cow sausage pork chop bacon hamburger. Spare ribs doner rump pork belly cow shoulder. Pork rump shoulder ground round strip steak, chuck short ribs hamburger pork belly bacon shankle. Doner chuck brisket strip steak.",
            "Ball tip biltong pork tri-tip filet mignon, jerky short ribs tail pig turducken. Rump cow beef ribs pork belly. Biltong short loin doner corned beef leberkas fatback landjaeger ribeye shoulder bacon shankle swine hamburger tenderloin. Salami venison rump leberkas ground round biltong pork swine, chuck bresaola ball tip corned beef pig andouille. Fatback venison ham jowl tongue beef ribs ribeye shoulder ground round.",
            "Frankfurter capicola shankle, kielbasa flank pork belly pork loin leberkas sausage porchetta. Shank tongue doner, pig boudin chuck jowl flank bresaola t-bone beef bacon pancetta pork belly. Short ribs tail bacon, spare ribs pancetta cow meatball corned beef sirloin. Corned beef boudin kevin, ground round ribeye chuck spare ribs kielbasa.",
            "Chuck sirloin strip steak ground round meatball, pork beef ribs ball tip kevin shankle spare ribs. Andouille rump biltong, sirloin pancetta ham hock strip steak swine chuck tri-tip ham short loin filet mignon jerky ribeye. Pork belly strip steak boudin, chicken turkey leberkas short ribs venison jerky corned beef kevin. Ball tip tongue leberkas cow. Shoulder flank pig ball tip cow kevin andouille, turkey venison frankfurter beef corned beef capicola ham. Boudin short ribs bacon jerky swine turducken tongue drumstick landjaeger. Jerky hamburger landjaeger tongue ham chuck andouille ground round frankfurter beef tail.",
            "Beef ribs bacon jowl bresaola tenderloin. Filet mignon chuck andouille pastrami. Ground round jowl pork strip steak landjaeger leberkas. Jowl flank rump pork belly pork loin pancetta brisket fatback pork turducken beef ball tip biltong tri-tip beef ribs. Jowl pork leberkas prosciutto, kielbasa meatball strip steak. Venison sausage pancetta landjaeger.",
            "Frankfurter andouille pastrami bresaola. Strip steak filet mignon corned beef t-bone short loin. Tail porchetta fatback, cow ham ribeye chicken shoulder sausage kielbasa meatloaf flank tongue pancetta hamburger. Corned beef bacon pig venison brisket. Jowl kevin strip steak chuck sausage."
    };

    public static Component createItem(int index) {
        CssLayout itemLayout = new CssLayout();
        itemLayout.setWidth("100%");

        boolean addImage = rand.nextFloat() < 0.66f;
        boolean addLabel = rand.nextFloat() < 0.66f;
        boolean addForm = !addImage && !addLabel;

        if(addImage && addLabel) {
            if(rand.nextFloat() < 0.75f) {
                Image image = getImage();
                image.addStyleName("add-space-after");
                itemLayout.addComponent(image);
                addImage = false;
            } else {
                Label label = getLabel();
                label.addStyleName("add-space-after");
                itemLayout.addComponent(label);
                addLabel = false;
            }
        }

        if(addImage) {
            itemLayout.addComponent(getImage());
        }

        if(addLabel) {
            itemLayout.addComponent(getLabel());
        }

        if(addForm) {
            VerticalLayout vl = new VerticalLayout();
            vl.setWidth("100%");
            vl.setSpacing(true);
            final TextField field = new TextField("Your name:");
            field.setWidth("100%");
            field.setImmediate(true);
            vl.addComponent(field);
            Button say = new Button("Say Hello", new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    String name = (field.getValue() != null && !(field.getValue().isEmpty())) ? field.getValue() : "Unknown";
                    Notification.show("Hello " + name);
                }
            });
            say.setWidth("100%");
            vl.addComponent(say);
            itemLayout.addComponent(vl);
        }

        if(rand.nextFloat() < 0.2f) {
            Link link = new Link("GitHub", new ExternalResource("https://github.com/alump/Masonry"));
            link.addStyleName("demo-link");
            link.addStyleName("add-space-before");
            itemLayout.addComponent(link);
        }

        return itemLayout;
    }

    public static Image getImage() {
        Image image = new Image();
        image.setSource(new ThemeResource("images/img" + rand.nextInt(10) + ".jpg"));
        image.setWidth("100%");
        return image;
    }

    public static Label getLabel() {
        Label label = new Label();
        label.setValue(BACON_IPSUMS[rand.nextInt(BACON_IPSUMS.length)]);
        return label;
    }

    public static Component createPostItNote() {
        return createPostItNote("remember to buy:<br/>• milk</br>• cheese</br>• and BEER!");
    }

    public static Component createPostItNote(String message) {
        CssLayout layout = new CssLayout();
        layout.setWidth("100%");
        layout.addStyleName("post-it");

        Label label = new Label(message);
        label.setContentMode(ContentMode.HTML);
        layout.addComponent(label);

        return layout;
    }

    public static Component createSlowImage(int index) {
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        vl.setSpacing(true);

        // Constructs delayed url
        String url = "http://deelay.me/";
        // Adds delay to url, also using index to make unique url to avoid caching
        url += (800 + (index * 10));
        url += "/http://misc.siika.fi/niinisto_kekkonen.jpg";

        Label label = new Label("This is slow image test...");
        vl.addComponent(label);

        Image image = new Image();
        image.addStyleName("slow-image");
        image.setSource(new ExternalResource(url));
        vl.addComponent(image);

        label = new Label("...it will take some time before this image is loaded. This is used to test relayouting.");
        vl.addComponent(label);

        return vl;
    }
}
