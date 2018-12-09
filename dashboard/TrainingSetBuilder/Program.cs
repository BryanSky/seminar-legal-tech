using System;
using System.Collections.Generic;
using System.IO;
using System.Text.RegularExpressions;

namespace TrainingSetBuilder {
    class Program {

        private static readonly string TrainDir = Environment.GetEnvironmentVariable("LocalAppData") + "/LegalTech/TrainingFiles";
        private static string testDir = Environment.GetEnvironmentVariable("LocalAppData") + "/LegalTech/TrainingFiles/TestFiles";

        static void Main(string[] args) {
            if (args.Length == 0) {
                string[] allFiles = null;
                if (Directory.Exists(TrainDir)) {
                    allFiles = Directory.GetFiles(TrainDir);
                }
                if (allFiles != null) {
                    foreach (var file in allFiles) {
                        List<string> result = new List<string>();
                        string fullText = ReadFullFile(file);
                        int index = 0;
                        while (index > -1) {
                            index = fullText.IndexOf("highlight7", ++index);
                            if (index > -1) {
                                string value = ExtractWord(fullText, index).Replace(" ", "").Replace("\r\n\r\n", "\r\n");
                                result.Add(value + "\tPerson");
                            }
                        }

                        index = 0;
                        while (index > -1) {
                            index = fullText.IndexOf("highlight3", ++index);
                            if (index > -1) {
                                string value = ExtractWord(fullText, index).Replace(" ", "").Replace("\r\n\r\n", "\r\n");
                                result.Add(value + "\tLocation");
                            }
                        }

                        index = 0;
                        while (index > -1) {
                            index = fullText.IndexOf("highlight4", ++index);
                            if (index > -1) {
                                string value = ExtractWord(fullText, index).Replace(" ", "").Replace("\r\n\r\n", "\r\n");
                                result.Add(value + "\tOrganisation");
                            }
                        }
                        //MatchCollection matches = Regex.Matches(fullText, "{\\/rtlch\\/fcs1 \\/af1\\/afs19 \\/ltrch\\/fcs0 \\/fs19\\/lang1024\\/langfe1024\\/highlight7\\/noproof\\/langnp1024\\/langfenp1024\\/insrsid15405279\\/charrsid+([0-9]*) ([A-Z]*[a-z]*)+}");
                        WriteResultToFile(result, file, ".tsv");
                        ConvertToTaggedFile(file);
                    }
                }
            }
            else {
                //handle console parameter
                //first parameter is either directory or single file
            }
            
        }

        private static void WriteResultToFile(List<string> result, string s, string fileExtension) {
            string filename = s.Replace(".rtf", fileExtension);
            File.WriteAllLines(filename, result);
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
            return Regex.Replace(formatedString.Substring(startIndex + 7, (formatedString.Length - (startIndex + 7))), "d[0-9]+", "");
        }

        private static string ReadFullFile(string filename) {
            return File.ReadAllText(filename);
        }

        private static void ConvertToTaggedFile(string file) {
            string fullText = ReadFullFile(file);
            int index = 0;
            while (index > -1) {
                index = fullText.IndexOf("highlight7", ++index);
                if (index > -1) {
                    int endIndex = fullText.IndexOf('}', index - 1);
                    string value = ExtractWord(fullText, index);
                    fullText = ReplaceWord(fullText, index, endIndex, value, "<PERSON>" + value + "</PERSON>");
                }
            }

            index = 0;
            while (index > -1) {
                index = fullText.IndexOf("highlight3", ++index);
                if (index > -1) {
                    int endIndex = fullText.IndexOf('}', index - 1);
                    string value = ExtractWord(fullText, index);
                    fullText = ReplaceWord(fullText, index, endIndex, value, "<LOCATION>" + value + "</LOCATION>");
                }
            }

            index = 0;
            while (index > -1) {
                index = fullText.IndexOf("highlight4", ++index);
                if (index > -1) {
                    int endIndex = fullText.IndexOf('}', index - 1);
                    string value = ExtractWord(fullText, index);
                    fullText = ReplaceWord(fullText, index, endIndex, value, "<ORGANISATION>" + value + "</ORGANISATION>");
                }
            }

            string correctedFile = fullText.Replace("highlight7", "\\highlight7").Replace("highlight3", "\\highlight3")
                .Replace("highlight4", "\\highlight4");
            File.WriteAllText(file.Replace(".rtf", "_TAGGED.rtf"), correctedFile);
        }

        private static string ReplaceWord(string fullText, int startIndex, int endIndex, string original, string replacement) {
            string substr = fullText.Substring(startIndex, endIndex - startIndex);
            string replaced = substr.Replace(original, replacement);
            return fullText.Substring(0, startIndex - 1) + replaced + fullText.Substring(endIndex);
        }
    }
}
