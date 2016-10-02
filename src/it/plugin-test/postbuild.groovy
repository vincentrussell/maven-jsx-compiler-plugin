import static org.junit.Assert.*


final File outputDir = new File(new File(basedir,"target"), "output")
final File output2Dir = new File(new File(basedir,"target"), "output2")

assertTrue(outputDir.exists());
assertTrue(output2Dir.exists());

def result1 = """
var profile = React.createElement(
  "div",
  null,
  React.createElement("img", { src: "avatar.png", className: "profile" }),
  React.createElement(
    "h3",
    null,
    [user.firstName, user.lastName].join(' ')
  )
);
""".trim();

assertEquals(result1,new File(outputDir,"test1.js").text.trim());
assertTrue(new File(output2Dir,"test2.js").exists());