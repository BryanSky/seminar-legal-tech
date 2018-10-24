using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Dashboard.Entities {
    public class LegalFile {
        private String filename;
        private List<Paragraph> paragraphs = new List<Paragraph>();

        public LegalFile(String content) {

        }

        public LegalFile(){

        }

        public List<NamedEntity> getNamedentities(){
            return new List<NamedEntity>();
        }
    }
}