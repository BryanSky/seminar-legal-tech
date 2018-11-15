using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace TrainingSetBuilder {
    class Program {

        private static string file = "";

        static void Main(string[] args) {
            List<string> result = new List<string>();
            string fullText = ReadFullFile(file);
            MatchCollection matches = Regex.Matches(fullText, "{\\/rtlch\\/fcs1 \\/af1\\/afs19 \\/ltrch\\/fcs0 \\/fs19\\/lang1024\\/langfe1024\\/highlight([0-9])\\/noproof\\/langnp1024\\/langfenp1024\\/insrsid15405279\\/charrsid([0-9]*) ([A-Z]*[a-z]*)}");
            foreach (var match in matches) {
                string rm = match.ToString().Replace("{\\rtlch\\fcs1 \\af1\\afs19 \\ltrch\\fcs0 \\fs19\\lang1024\\langfe1024\\", "");
                rm = rm.Replace("\\/noproof\\/langnp1024\\/langfenp1024\\/insrsid15405279\\/charrsid", ";");
                string[] parts = rm.Split(';');
                string formatNumber = parts[0].Replace("highlight", "");
                string content = parts[1].Replace("}", "").Substring(8);
                result.Add(content + "\t" + formatNumber);
            }
        }

        private static string ReadFullFile(string filename) {
            return System.IO.File.ReadAllText(filename);
        }
    }
}
