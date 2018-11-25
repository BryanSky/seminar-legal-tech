using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dashboard.Entities;

namespace Dashboard.Models
{
    public class StartupViewModel {

        private static string TYPE_PERSON = "Pers";
        private static string TYPE_ORGANISATION = "Org";
        private static string TYPE_LOCATION = "Loc";

        public List<string> PersonList = new List<string>();
        public List<string> OrganisationList = new List<string>();
        public List<string> LocationList = new List<string>();
        public List<string> TagList = new List<string>();
        public List<ResultTableRow> Results = new List<ResultTableRow>();

        public void LoadData() {
            //get values for person, organisation and location
            //LoadFilteredData();
        }

        public void LoadFilteredData(string person, string organisation, string location) {
            if (person == null) {
                LoadFilteredData(organisation, TYPE_ORGANISATION, location, TYPE_LOCATION);
            } else if (organisation == null) {
                LoadFilteredData(person, TYPE_PERSON, location, TYPE_LOCATION);
            }else if (location == null) {
                LoadFilteredData(person, TYPE_PERSON, organisation, TYPE_ORGANISATION);
            }
            else {
                string sql = "SELECT * FROM <> WHERE ";
                Results = DbManager.GetData(sql);
            }
        }

        private void LoadFilteredData(string e1, string e1Type, string e2, string e2Type) {
            if (e1 == null) {
                LoadFilteredData(e2, e2Type);
            }else if (e2 == null) {
                LoadFilteredData(e1, e1Type);
            }
            else {
                string sql = "";
                Results = DbManager.GetData(sql);
            }
        }

        private void LoadFilteredData(string e, string eType) {
            if (e == null) return;
            string sql = "";
            Results = DbManager.GetData(sql);
        }
    }
}
