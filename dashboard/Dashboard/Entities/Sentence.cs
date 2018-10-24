using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Dashboard.Entities {
    public class Sentence {
        private List<NamedEntity> entities = new List<NamedEntity>();

        public Sentence(string[] content) {
            foreach (var s in content) {
                entities.Add(new NamedEntity(s));
            }
        }

        public Sentence() {

        }
    }
}