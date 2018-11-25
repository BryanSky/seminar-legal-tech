using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;

namespace TrainingSetBuilder {
    class Program {

        private static string dir = "C:\\Users\\bened\\Desktop\\TrainingFiles";

        static void Main(string[] args) {
            string[] allFiles = null;
            if (Directory.Exists(dir)) {
                allFiles = Directory.GetFiles(dir);
            }
            if (allFiles != null) {
                foreach (var file in allFiles) {
                    List<string> result = new List<string>();
                    string fullText = ReadFullFile(file);
                    int index = 0;
                    while (index > -1) {
                        index = fullText.IndexOf("highlight7", ++index);
                        if (index > -1) {
                            string value = ExtractWord(fullText, index);
                            result.Add(value + "\tPerson");
                        }
                    }

                    index = 0;
                    while (index > -1) {
                        index = fullText.IndexOf("highlight3", ++index);
                        if (index > -1) {
                            string value = ExtractWord(fullText, index);
                            result.Add(value + "\tLocation");
                        }
                    }

                    index = 0;
                    while (index > -1) {
                        index = fullText.IndexOf("highlight4", ++index);
                        if (index > -1) {
                            string value = ExtractWord(fullText, index);
                            result.Add(value + "\tOrganisation");
                        }
                    }
                    //MatchCollection matches = Regex.Matches(fullText, "{\\/rtlch\\/fcs1 \\/af1\\/afs19 \\/ltrch\\/fcs0 \\/fs19\\/lang1024\\/langfe1024\\/highlight7\\/noproof\\/langnp1024\\/langfenp1024\\/insrsid15405279\\/charrsid+([0-9]*) ([A-Z]*[a-z]*)+}");
                    WriteResultToFile(result, file);
                }
            }
        }

        private static void WriteResultToFile(List<string> result, string s) {
            string filename = s.Replace(".rtf", ".tsv");
            System.IO.File.WriteAllLines(filename, result);
        }

        private static string ExtractWord(string fullText, int index) {
            int origIndex = index;
            int startIndex = fullText.IndexOf('{', index-1);
            int endIndex = -1;
            endIndex = fullText.IndexOf('}', index-1);
            while (startIndex > origIndex) {
                startIndex = fullText.IndexOf('{', index--);
            }
            string formatedString = fullText.Substring(startIndex, endIndex - startIndex);
            startIndex = formatedString.IndexOf("charrsid");
            //return formatedString.Substring(startIndex + 7, (formatedString.Length - (startIndex + 7)));
            return Regex.Replace(formatedString.Substring(startIndex + 7, (formatedString.Length - (startIndex + 7))), "d[0-9]*", "");
        }

        private static string ReadFullFile(string filename) {
            return System.IO.File.ReadAllText(filename);
        }
    }
}
