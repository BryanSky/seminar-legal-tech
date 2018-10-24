using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Dashboard.Entities {
    public class Paragraph
    {
        private List<Sentence> content = new List<Sentence>();

        public Paragraph(List<Sentence> content) {
            this.content = content;
        }

        public Paragraph() { }

        public void Add(Sentence sentence) {
            content.Add(sentence);
        }

        public Sentence GetElementAtPosition(int index) {
            return content.ElementAt(index);
        }
    }
}