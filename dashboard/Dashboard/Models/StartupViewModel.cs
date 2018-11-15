using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dashboard.Entities;

namespace Dashboard.Models
{
    public class StartupViewModel
    {
        public List<string> klaegerList = new List<string>();
        public List<string> themeList = new List<string>();
        public List<string> targetList = new List<string>();
        public List<string> tagList = new List<string>();
        public List<ResultTableRow> results = new List<ResultTableRow>();

        public void LoadData() {

        }
    }
}
