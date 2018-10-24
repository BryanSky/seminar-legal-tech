using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Dashboard.Entities {
    public class NamedEntity {

        public string Value { get; set; }
        public EntityLabel Class { get; set; }

        public NamedEntity(string value)
        {
            this.Value = value;
        }

        public NamedEntity(){}
    }

    public enum EntityLabel {
        PERSON,
        LOCATION,
        OBJECT,
        NONE
    }
}