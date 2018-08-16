using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace WPFDatingApp
{
    class XmlDoc
    {
        private XmlDocument document = null;
        private Boolean hasError = false;
        public XmlDoc(String xmlDocString)
        {
            document = new XmlDocument();
            try
            {
                document.Load(xmlDocString);
                XmlNode errorNode = document.SelectSingleNode("//ERROR");
                if(errorNode != null)
                {
                    this.hasError = true;
                }
            }
            catch (Exception e)
            {
                error("Invalid XML");
            }
        }

        public XmlDoc(XmlDocument document)
        {
            this.document = document;
        }

        public XmlDocument getDocument()
        {
            return this.document;
        }

        public void error(String errorMessage)
        {
            this.hasError = true;
            document = new XmlDocument();
            try
            {
                document.Load("<? xml version =\"1.0\"?> \n" + 
                    "<PAGE><ERROR><ERROR_MESSAGE>" +
                    errorMessage +
                    "</ERROR_MESSAGE></ERROR></PAGE>");
            }
            catch (Exception e)
            {
                error("Invalid XML");
            }
        }

        public Boolean getHasError()
        {
            return this.hasError;
        }

        public String AsXML()
        {
            return this.document.ToString();
        }
    }
}
